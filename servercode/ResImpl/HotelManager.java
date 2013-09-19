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
            putHotel(id, curObj.getKey(), curObj);
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
    public boolean reserveRoom(int id, Customer customer, String itemId)
        throws RemoteException {
        Hotel curObj = fetchHotel(id, itemId);
        if (curObj == null) {
            return false;
        }
        else if (curObj.getCount() == 0) {
            return false;
        }
        else {
            // ?????
            String key = Hotel.getKey(itemId);
            customer.reserve(key, itemId, curObj.getPrice());
            putHotel(id, customer.getKey(), customer);

            // decrease the number of available items in the storage
            curObj.setCount(curObj.getCount() - 1);
            curObj.setReserved(curObj.getReserved() + 1);

            return true;
        }
    }
    
    private Hotel fetchHotel(int id, String itemId) {
        synchronized (roomsTable) {
            return (Hotel)roomsTable.get(itemId);
        }
    }
    
    private void putHotel(int id, String itemId, RMItem value) {
        synchronized (roomsTable) {
            roomsTable.put(itemId, value);
        }
    }
    
    private void deleteHotel(int id, String itemId) {
        synchronized (roomsTable) {
            roomsTable.remove(itemId);
        }
    }


}
