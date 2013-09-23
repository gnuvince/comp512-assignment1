package comp512;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Comm {
    /**
     * Read an object from a socket, returning null in case of exceptions.
     * @param connection
     * @return
     */
    public static Object recvObject(Socket connection) {
        try {
            ObjectInputStream ois = new ObjectInputStream(connection.getInputStream());
            Object o = ois.readObject();
            return o;
        }
        catch (IOException e) {
            System.err.println("Error reading object: " + e.getMessage());
            return null;
        }
        catch (ClassNotFoundException e) {
            System.err.println("Cannot recreate object");
            return null;
        }
    }
    
    /**
     * Send an object to a socket.
     * @param connection
     * @param o
     * @throws IOException
     */
    public static void sendObject(Socket connection, Object o) {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(connection.getOutputStream());
            oos.writeObject(o);
        }
        catch (IOException e) {
            System.err.println("Cannot send object: " + e.getMessage());
        }
    }
    
}
