import hash.MapProxy;
import raft.core.RaftClientService;
import raft.core.StateMachine;
import raft.core.client.RaftClientServiceImpl;
import utils.TimeUnitMap;

/**
 * @author ruanxin
 * @create 2018-04-23
 * @desc state machine
 */
public class CheetahStateMachine implements StateMachine {

    private MapProxy mapProxy;
    private RaftClientService raftClientService;

    public CheetahStateMachine () {
        mapProxy = new MapProxy();
        raftClientService = new RaftClientServiceImpl();
    }

    public CheetahStateMachine (MapProxy mapProxy, RaftClientService raftClientService) {
        this.mapProxy = mapProxy;
        this.raftClientService = raftClientService;
    }

    @Override
    public void submit(byte[] data) {
        String command = new String(data);
        String[] commandArr = command.split("\\ ");
        boolean result;
        //set key value 5 mm
        if (commandArr.length == 3) {
            result = mapProxy.set(commandArr[1], commandArr[2].getBytes());
        } else if (commandArr.length == 4) {
            result = mapProxy.set(commandArr[1], commandArr[2].getBytes(),
                    Integer.parseInt(commandArr[3]));
        } else if (commandArr.length == 5) {
            result = mapProxy.set(commandArr[1], commandArr[2].getBytes(),
                    Integer.parseInt(commandArr[3]), TimeUnitMap.tuMap.get(commandArr[4]).getTimeUnit());
        } else {
            result = false;
        }
    }

    @Override
    public byte[] get(String key) {
        return mapProxy.get(key);
    }


}
