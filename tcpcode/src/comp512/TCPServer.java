package comp512;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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

public class TCPServer {
    private Map<String, HostPort> backends;
    
    public TCPServer() {
        backends = new HashMap<String, HostPort>();
    }
    
    public static void main(String[] args) {
        if (args.length != 3) {
            System.err.println("Usage: TCPServer car=<host>:<port> flight=<host>:<port> hotel=<host>:<port>");
            System.exit(1);
        }
        
        TCPServer obj = new TCPServer();
        obj.populateBackends(args);
        
        ExecutorService executor = Executors.newFixedThreadPool(16);
        ServerSocket server;
        try {
            server = new ServerSocket(5566);
            while (true) {
                // Accept connection from a new client
                final Socket connection;
                try {
                    connection = server.accept();
                    System.out.println("Connection from "
                        + connection.getRemoteSocketAddress());
                }
                catch (IOException e) {
                    System.err.println("Error in server.accept(): "
                        + e.getMessage());
                    continue;
                }

                ArrayList<String> msg = obj.readMessage(connection);
                System.out.println(msg);
                obj.sendResponse(connection, msg.get(0).charAt(0)== 'n' ? 1 : 0);
            }
        }
        catch (IOException e) {
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
    
    
    /**
     * Read an ArrayList<String> from a client connection.
     * @param connection The client connection.
     * @return The array list if successful, null otherwise.
     */
    public ArrayList<String> readMessage(Socket connection) {
        try {
            ObjectInputStream clientInput = new ObjectInputStream(
                connection.getInputStream()
                );
            ArrayList<String> msg = (ArrayList<String>)clientInput.readObject();
            return msg;
        }
        catch (ClassNotFoundException e) {
            System.err.println(e.toString());
            return null;
        }
        catch (IOException e) {
            System.err.println(e.toString());
            return null;
        }
    }
    
    
    public void sendResponse(Socket connection, int ok) {
        try {
            OutputStream os = connection.getOutputStream();
            os.write(ok);
            os.close();
        }
        catch (IOException e) {
            System.err.println(e.toString());
        }
    }
}
