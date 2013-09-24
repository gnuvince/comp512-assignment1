package ResImpl;

import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import ResInterface.ItemManager;
import ResInterface.ResourceManager;

public class HotelManagerImpl implements ItemManager {
    
    protected RMHashtable roomsTable = new RMHashtable();
    
    public static void main(String args[]) {
        // Figure out where server is running
        String server = "localhost";
        int port = 5005;

        if (args.length == 1) {
            server = server + ":" + args[0];
        } else if (args.length != 0 &&  args.length != 1) {
            System.err.println ("Wrong usage");
            System.out.println("Usage: java ResImpl.HotelManagerImpl [port]");
            System.exit(1);
        }

        try 
        {
            // create a new Server object
        	HotelManagerImpl obj = new HotelManagerImpl();
            // dynamically generate the stub (client proxy)
            ItemManager rm = (ItemManager) UnicastRemoteObject.exportObject(obj, 0);

            // Bind the remote object's stub in the registry
            Registry registry = LocateRegistry.getRegistry(port);
            registry.rebind("Group5_HotelManager", rm);

            System.err.println("Hotel Server ready");
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
    public boolean addItem(int id, String location, int quantity, int price)
        throws RemoteException {
    	
        Hotel curObj = (Hotel) fetchHotel(id, Hotel.getKey(location));
        if (curObj == null) {
            // If hotel doesn't exist, create it and add it to 
            // the manager's hash table.
            Hotel newObj = new Hotel(location, quantity, price);
            putHotel(id, newObj.getKey(), newObj);
        }
        else {
            // If the hotel already exists, update its quantity (by adding
            // the new quantity) and update its price (only if the new price
            // is positive).
            curObj.setCount(curObj.getCount() + quantity);
            if (price > 0) {
                curObj.setPrice(price);
            }
            putHotel(id, Hotel.getKey(location), curObj);
        }
        
        System.out.println("Hotel " + location + " was added");
        
        return true;
    }

    @Override
    public boolean deleteItem(int id, String location) throws RemoteException {
    	
    	String itemId = Hotel.getKey(location);
        Hotel curObj = fetchHotel(id, itemId);
                
        if (curObj == null) {
        	System.out.println("The hotel you are trying to delete doesn't exist " + itemId);
            return false;
        }
        else {
            if (curObj.getReserved() == 0) {
                deleteHotel(id, curObj.getKey());
                System.out.println("Hotel " + itemId + " deleted successfully");
                return true;
            }
            else {
            	System.out.println("Hotel " + itemId + " can't be deleted because a customer reserved it!" );
                return false;
            }
        } 
    }

    @Override
    public int queryItemQuantity(int id, String location) throws RemoteException {
        Hotel curObj = fetchHotel(id, Hotel.getKey(location));
        if (curObj != null) {
            return curObj.getCount();
        }
        
        return 0;
    }

    @Override
    public int queryItemPrice(int id, String location) throws RemoteException {
        Hotel curObj = fetchHotel(id, Hotel.getKey(location));
        if (curObj != null) {
            return curObj.getPrice();
        }
        return 0;   
    }

   
    @Override
    public ReservedItem reserveItem(int id, String customerId, String location)
        throws RemoteException {
    	    	
        Hotel curObj = fetchHotel(id, Hotel.getKey(location));
        
        if (curObj == null) {
        	System.out.println("Room in " + location + " couldn't be reserved because none exists");
            return null;
        }
        else if (curObj.getCount() == 0) {
        	System.out.println("Room in " + location + " couldn't be reserved because they are all reserved");
            return null;
        }
        else {        	
            String key = Hotel.getKey(location);
            
            // decrease the number of available items in the storage
            curObj.setCount(curObj.getCount() - 1);
            curObj.setReserved(curObj.getReserved() + 1);

            putHotel(id, key, curObj);
            
            System.out.println("Customer " + customerId + " reserved a room in " + location);
            return new ReservedItem(key, curObj.getLocation(), 1, curObj.getPrice());
        }
    }
    
    public boolean cancelItem(int id, String hotelKey, int count)
    	throws RemoteException {
    	
    	System.out.println("cancelItem( " + id + ", " + hotelKey + ", " + count + " )");
    	    	
    	Hotel curObj = fetchHotel(id, hotelKey);
    	if (curObj == null) {
    		System.out.println("Room " + hotelKey + " can't be cancelled because none exists");
    		return false;
    	}
    	
    	//adjust available quantity
    	curObj.setCount(curObj.getCount() + count);
    	curObj.setReserved(curObj.getReserved() - count);
    	
    	return true;
    }
    
    private Hotel fetchHotel(int id, String itemId) {
        synchronized (roomsTable) {
            return (Hotel)roomsTable.get(itemId);
        }
    }
    
    private void putHotel(int id, String itemId, Hotel hotel) {
        synchronized (roomsTable) {
            roomsTable.put(itemId, hotel);            
        }
    }
    
    private void deleteHotel(int id, String itemId) {
        synchronized (roomsTable) {
            roomsTable.remove(itemId);
        }
    }


}
