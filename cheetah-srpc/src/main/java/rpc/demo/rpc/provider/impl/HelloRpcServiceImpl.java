package rpc.demo.rpc.provider.impl;

import org.apache.log4j.Logger;
import rpc.RpcContext;
import rpc.demo.rpc.provider.HelloRpcService;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author:Roy
 * @Date: Created in 17:11 2017/12/3 0003
 */
public class HelloRpcServiceImpl implements HelloRpcService {
    private Logger logger = Logger.getLogger(HelloRpcServiceImpl.class);

    List<String> list = new ArrayList<String>();

    public void sayHello(String message,int tt) {
        Object attachment = RpcContext.getContext().getAttachment("myattachment");
        System.out.println("my attachment:"+attachment);
        System.out.println("sayHello:"+message+" intValue:"+tt);
    }

    public String getHello() {
        return "this is hello service";
    }

    public int callException(boolean exception) {
        if(exception){
            throw new RuntimeException("happen");
        }
        return 1;
    }

    public synchronized void addMessage(String message) {
        list.add(message);
    }

    // TODO: 2018/2/10 并发 出现读是修改
    public synchronized void printList() {
        for (String message : list) {
            System.out.println("-------" + message + "-----------");
        }
    }

    public synchronized void printListSize() {
        System.out.println(list.size());
    }
}
