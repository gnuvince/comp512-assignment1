package ResImpl;

import java.rmi.RemoteException;

import ResInterface.ItemManager;

public class HotelManager implements ItemManager {
    
    protected RMHashtable roomsTable = new RMHashtable();

    @Override
    public boolean addItem(int id, String itemId, int quantity, int price)
        throws RemoteException {
        Hotel curObj = (Hotel) fetchHotel(id, Hotel.getKey(itemId));
        if (curObj == null) {
            // If hotel doesn't exist, create it and add it to 
            // the manager's hash table.
            Hotel newObj = new Hotel(itemId, quantity, price);
            putHotel(id, newObj.getKey(), "need customer id");
        }
        else {
            // If the hotel already exists, update its quantity (by adding
            // the new quantity) and update its price (only if the new price
            // is positive).
            curObj.setCount(curObj.getCount() + quantity);
            if (price > 0) {
                curObj.setPrice(price);
            }
            putHotel(id, itemId, "need customer id");
        }
        return true;
    }

    @Override
    public boolean deleteItem(int id, String itemId) throws RemoteException {
        Hotel curObj = fetchHotel(id, itemId);
        if (curObj == null) {
            return false;
        }
        else {
            if (curObj.getReserved() == 0) {
                deleteHotel(id, curObj.getKey());
                return true;
            }
            else {
                return false;
            }
        } 
    }

    @Override
    public int queryItemQuantity(int id, String itemId) throws RemoteException {
        Hotel curObj = fetchHotel(id, itemId);
        if (curObj != null) {
            return curObj.getCount();
        }
        return 0;
    }

    @Override
    public int queryItemPrice(int id, String itemId) throws RemoteException {
        Hotel curObj = fetchHotel(id, itemId);
        if (curObj != null) {
            return curObj.getPrice();
        }
        return 0;   
    }

   
    @Override
    public ReservedItem reserveRoom(int id, String customerId, String itemId)
        throws RemoteException {
        Hotel curObj = fetchHotel(id, itemId);
        if (curObj == null) {
            return null;
        }
        else if (curObj.getCount() == 0) {
            return null;
        }
        else {
            // ?????
            String key = Hotel.getKey(itemId);
            //customer.reserve(key, itemId, curObj.getPrice());
            putHotel(id, itemId, customerId);

            // decrease the number of available items in the storage
            curObj.setCount(curObj.getCount() - 1);
            curObj.setReserved(curObj.getReserved() + 1);

            
            return new ReservedItem(key, curObj.getLocation(), 1, curObj.getPrice());
        }
    }
    
    private Hotel fetchHotel(int id, String itemId) {
        synchronized (roomsTable) {
            return (Hotel)roomsTable.get(itemId);
        }
    }
    
    private void putHotel(int id, String itemId, String customerId) {
        synchronized (roomsTable) {
            roomsTable.put(itemId, customerId);
        }
    }
    
    private void deleteHotel(int id, String itemId) {
        synchronized (roomsTable) {
            roomsTable.remove(itemId);
        }
    }


}
