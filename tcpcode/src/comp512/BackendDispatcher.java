package comp512;

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
        Result r = new Result();
        r.boolResult = true;
        return r;
    }

}
