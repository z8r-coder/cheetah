import hash.MapProxy;
import raft.core.RaftClientService;
import raft.core.StateMachine;
import raft.core.client.RaftClientServiceImpl;
import raft.protocol.response.GetLeaderResponse;

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
        if (commandArr[0].equals("get")) {
            if (commandArr[1].equals("leader")) {
                if (commandArr.length > 2) {
                    System.out.println("wrong syntax!");
                } else {
                    //execute command
                    GetLeaderResponse response = raftClientService.getLeader();
                    if (response == null) {
                        System.out.println("request time out!");
                    } else {
                        System.out.println(response.getLeaderId());
                    }
                }
            } else if (commandArr[0].equals("set")) {

            }
        }
    }
}
