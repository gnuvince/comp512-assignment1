package comp512;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.Callable;

public class BackendDispatcher implements Callable<Result> {
    private ArrayList<String> msg;
    private Map<String, HostPort> backends;
    
    public BackendDispatcher(ArrayList<String> msg, Map<String, HostPort> backends) {
        this.msg = msg;
        this.backends = backends;
    }
    
    public Result call() {
        String cmd = this.msg.get(0);
        Socket socket;
        HostPort hp = null;
        

        if (cmd.contains("flight")) {
            hp = this.backends.get("flight");
            
        }
        else if (cmd.contains("car")) {
            hp = this.backends.get("car");
        }
        else if (cmd.contains("room")) {
            hp = this.backends.get("hotel");

        }
        else if (cmd.contains("customer")) {
            hp = this.backends.get("customer");

        }
        else if (cmd.contains("itinerary")) {
            // TODO: send reserve requests to all backends.
            return null;
        }
        
        try {
            socket = new Socket(hp.host, hp.port);
            Comm.sendObject(socket, this.msg);
            Result res = (Result)Comm.recvObject(socket);
            socket.close();
            return res;
        }
        catch (IOException e) {
            return null;
        }
    }
}
