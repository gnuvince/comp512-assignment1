package comp512;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

class HostPort {
    public String host;
    public int port;
    
    public HostPort(String host, int port) {
        this.host = host;
        this.port = port;
    }
}

public class TCPMiddleWare {
    private Map<String, HostPort> backends;
    
    public TCPMiddleWare() {
        backends = new HashMap<String, HostPort>();
    }
    
    public static void main(String[] args) {
        if (args.length != 3) {
            System.err.println("Usage: TCPMiddleWare car=<host>:<port> flight=<host>:<port> hotel=<host>:<port>");
            System.exit(1);
        }
        
        ExecutorService executor = Executors.newFixedThreadPool(16);
        TCPMiddleWare server = new TCPMiddleWare();
        server.populateBackends(args);
        ServerSocket serverSocket;
        
        try {
            serverSocket = new ServerSocket(5566);
            while (true) {
                // Accept connection from a new client
                final Socket connection;
                try {
                    connection = serverSocket.accept();
                    System.out.println("Connection from "
                        + connection.getRemoteSocketAddress());
                }
                catch (IOException e) {
                    System.err.println("Error in server.accept(): "
                        + e.getMessage());
                    continue;
                }

                ArrayList<String> msg = (ArrayList<String>)Comm.recvObject(connection);
                System.out.println(msg);

                // The following steps happen here:
                // 1. An asynchronous request is send to the dispatcher.
                // 2. While the dispatcher is doing its work, a new asynchronous
                //    task is started that'll wait for the result of the dispatcher
                //    and then send the result back to the client.
                // 3. While these two threads are running, the middleware listens
                //    for new connections.
                final Future<Result> resultFuture = executor.submit(
                    new BackendDispatcher(msg, server.backends));
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        Result result;
                        try {
                            result = resultFuture.get();
                            
                            // We got a result back from a reservation command,
                            // we now need to send it to the Customer backend.
                            if (result.reservationResult != null) {
                                
                            }
                            
                            Comm.sendObject(connection, result);
                            connection.close();
                        }
                        catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
        catch (IOException e) {
            System.err.println(e.getMessage());
            System.err.println("Cannot create a new socket on port 5566.");
            System.exit(1);
        }
    }

    /**
     * Add the backends with their associated host/port information
     * to the backends map.
     * @param args array of mappings of the form: backend=host:port
     */
    public void populateBackends(String[] args) {
        for (String arg: args) {
            String[] keyValue = arg.split("=");
            String[] hostPort = keyValue[1].split(":");
            backends.put(keyValue[0], 
                new HostPort(hostPort[0], Integer.parseInt(hostPort[1])));
        }
    }
    
}
