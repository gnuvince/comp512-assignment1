package ResImpl;

import java.rmi.RemoteException;

import ResInterface.ItemManager;

public class CustomerManagerImpl implements ItemManager {

    @Override
    public boolean addItem(int id, String itemId, int quantity, int price)
        throws RemoteException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean deleteItem(int id, String itemId) throws RemoteException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public int queryItemQuantity(int id, String itemId) throws RemoteException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int queryItemPrice(int id, String itemId) throws RemoteException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public ReservedItem reserveItem(int id, String customerId, String itemId)
        throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean cancelItem(int id, String itemId, int count)
        throws RemoteException {
        // TODO Auto-generated method stub
        return false;
    }

}
