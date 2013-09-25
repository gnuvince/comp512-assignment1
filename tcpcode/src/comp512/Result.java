package comp512;

import java.io.Serializable;

import ResImpl.ReservedItem;

@SuppressWarnings("serial")
public class Result implements Serializable {
    public Boolean boolResult; // For commands that either succeed or fail
    public Integer intResult; // For query commands
    public ReservedItem reservationResult; // For reserve commands
    
    public Result() {
        boolResult = null;
        intResult = null;
        reservationResult = null;
    }
}
