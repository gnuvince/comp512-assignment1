package comp512;

import java.io.Serializable;

import ResImpl.ReservedItem;

@SuppressWarnings("serial")
public class Result implements Serializable {
    public Boolean boolResult;
    public ReservedItem reservationResult;
    
    public Result() {
        boolResult = null;
        reservationResult = null;
    }
}
