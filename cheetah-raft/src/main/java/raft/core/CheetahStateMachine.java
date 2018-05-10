package raft.core;

import hash.MapProxy;
import utils.TimeUnitMap;

/**
 * @author ruanxin
 * @create 2018-04-23
 * @desc state machine
 */
public class CheetahStateMachine implements StateMachine {

    private MapProxy mapProxy;

    public CheetahStateMachine () {
        mapProxy = new MapProxy();
    }

    public CheetahStateMachine (MapProxy mapProxy, RaftClientService raftClientService) {
        this.mapProxy = mapProxy;
    }

    @Override
    public void submit(byte[] data) {
        String command = new String(data);
        String[] commandArr = command.split("\\ ");
        //set key value 5 mm
        if (commandArr.length == 3) {
            mapProxy.set(commandArr[1], commandArr[2].getBytes());
        } else if (commandArr.length == 4) {
            mapProxy.set(commandArr[1], commandArr[2].getBytes(),
                    Integer.parseInt(commandArr[3]));
        } else if (commandArr.length == 5) {
            mapProxy.set(commandArr[1], commandArr[2].getBytes(),
                    Integer.parseInt(commandArr[3]), TimeUnitMap.tuMap.get(commandArr[4]).getTimeUnit());
        } else {
            //threw ex
        }
    }

    @Override
    public byte[] get(String key) {
        return mapProxy.get(key);
    }

    @Override
    public void stop() {
        mapProxy.stop();
    }


}
