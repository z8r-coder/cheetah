package raft.demo.statemachine;

import raft.core.StateMachine;

/**
 * @author ruanxin
 * @create 2018-04-18
 * @desc
 */
public class ExampleStateMachine implements StateMachine {
    @Override
    public void submit(byte[] data) {
        String test = new String(data);
        System.out.println(test);
    }

    @Override
    public byte[] get(String key) {
        return new byte[0];
    }
}
