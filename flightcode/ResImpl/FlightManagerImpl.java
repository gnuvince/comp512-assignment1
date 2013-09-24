package ResImpl;

import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import ResInterface.ItemManager;

public class FlightManagerImpl implements ItemManager {
    
    protected RMHashtable flightTable = new RMHashtable();
    
    public static void main(String args[]) {
        // Figure out where server is running
        String server = "localhost";
        int port = 5007;

        if (args.length == 1) {
            server = server + ":" + args[0];
        } else if (args.length != 0 &&  args.length != 1) {
            System.err.println ("Wrong usage");
            System.out.println("Usage: java ResImpl.FlightManagerImpl [port]");
            System.exit(1);
        }

        try 
        {
            // create a new Server object
        	FlightManagerImpl obj = new FlightManagerImpl();
            // dynamically generate the stub (client proxy)
            ItemManager rm = (ItemManager) UnicastRemoteObject.exportObject(obj, 0);

            // Bind the remote object's stub in the registry
            Registry registry = LocateRegistry.getRegistry(port);
            registry.rebind("Group5_FlightManager", rm);

            System.err.println("Flight Server ready");
        } 
        catch (Exception e) 
        {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }

        // Create and install a security manager
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new RMISecurityManager());
        }
    }

    @Override
    public boolean addItem(int id, String flightNum, int flightSeats, int flightPrice)
        throws RemoteException {
    	
    	int nflightNum = Integer.valueOf(flightNum);
    	
        Flight curObj = (Flight) fetchFlight(id, Flight.getKey(nflightNum));
        if (curObj == null) {
            // If Flight doesn't exist, create it and add it to 
            // the manager's hash table.
            Flight newObj = new Flight(nflightNum, flightSeats, flightPrice);
            putFlight(id, newObj.getKey(), newObj);
            Trace.info("RM::addFlight(" + id + ") created new flight "
                    + flightNum + ", seats=" + flightSeats + ", price=$" + flightPrice);
        }
        else {
            // If the Flight already exists, update its quantity (by adding
            // the new quantity) and update its price (only if the new price
            // is positive).
            curObj.setCount(curObj.getCount() + flightSeats);
            if (flightPrice > 0) {
                curObj.setPrice(flightPrice);
            }
            putFlight(id, Flight.getKey(nflightNum), curObj);
            
            Trace.info("RM::addFlight(" + id + ") modified existing flight "
                    + flightNum + ", seats=" + curObj.getCount() + ", price=$"
                    + curObj.getPrice());
        }
                
        return true;
    }

    @Override
    public boolean deleteItem(int id, String flightNum) throws RemoteException {
    	
    	int nflightNum = Integer.valueOf(flightNum);
    	
    	String itemId = Flight.getKey(nflightNum);
        Flight curObj = fetchFlight(id, itemId);
                
        if (curObj == null) {
            Trace.warn("RM::deleteItem(" + id + ", " + itemId
                    + ") failed--item doesn't exist");        	
            return false;
        }
        else {
            if (curObj.getReserved() == 0) {
                deleteFlight(id, curObj.getKey());
                Trace.info("RM::deleteItem(" + id + ", " + itemId
                        + ") item deleted");
                return true;
            }
            else {
            	Trace.info("RM::deleteItem("
                        + id
                        + ", "
                        + itemId
                        + ") item can't be deleted because some customers reserved it");
                return false;
            }
        } 
    }

    @Override
    public int queryItemQuantity(int id, String flightNum) throws RemoteException {
    	int nflightNum = Integer.valueOf(flightNum);
    	
        Flight curObj = fetchFlight(id, Flight.getKey(nflightNum));
        if (curObj != null) {
            return curObj.getCount();
        }
        
        return 0;
    }

    @Override
    public int queryItemPrice(int id, String flightNum) throws RemoteException {
    	int nflightNum = Integer.valueOf(flightNum);
    	
        Flight curObj = fetchFlight(id, Flight.getKey(nflightNum));
        if (curObj != null) {
            return curObj.getPrice();
        }
        return 0;   
    }

   
    @Override
    public ReservedItem reserveItem(int id, String customerId, String flightNum)
        throws RemoteException {
    	int nflightNum = Integer.valueOf(flightNum);
    	
    	Flight curObj = fetchFlight(id, Flight.getKey(nflightNum));
        
        if (curObj == null) {
        	Trace.warn("RM::reserveItem( " + id + ", " + customerId + ", " + flightNum + ") failed--item doesn't exist");        	
            return null;
        }
        else if (curObj.getCount() == 0) {
        	Trace.warn("RM::reserveItem( " + id + ", " + customerId + ", " + flightNum + ") failed--No more items");        	
            return null;
        }
        else {        	
            String key = Flight.getKey(nflightNum);
            
            // decrease the number of available items in the storage
            curObj.setCount(curObj.getCount() - 1);
            curObj.setReserved(curObj.getReserved() + 1);

            putFlight(id, key, curObj);
            
            Trace.info("RM::reserveItem( " + id + ", " + customerId + ", " + key + ") succeeded");            
            
            return new ReservedItem(key, curObj.getLocation(), 1, curObj.getPrice());
        }
    }
    
    public boolean cancelItem(int id, String flightKey, int count)
    	throws RemoteException {
    	
    	System.out.println("cancelItem( " + id + ", " + flightKey + ", " + count + " )");
    	    	
    	Flight curObj = fetchFlight(id, flightKey);
    	if (curObj == null) {
    		Trace.warn("Flight " + flightKey + " can't be cancelled because item doesn't exists");
    		return false;
    	}
    	
    	//adjust available quantity
    	curObj.setCount(curObj.getCount() + count);
    	curObj.setReserved(curObj.getReserved() - count);
    	
    	Trace.info("Reservation of flight " + flightKey + " cancelled.");
    	
    	return true;
    }
    
    private Flight fetchFlight(int id, String itemId) {
        synchronized (flightTable) {
            return (Flight)flightTable.get(itemId);
        }
    }
    
    private void putFlight(int id, String itemId, Flight Flight) {
        synchronized (flightTable) {
        	flightTable.put(itemId, Flight);            
        }
    }
    
    private void deleteFlight(int id, String itemId) {
        synchronized (flightTable) {
        	flightTable.remove(itemId);
        }
    }


}
