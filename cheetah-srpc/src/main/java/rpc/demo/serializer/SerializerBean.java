package rpc.demo.serializer;

import java.io.Serializable;

/**
 * @Author:Roy
 * @Date: Created in 23:37 2017/11/7 0007
 */

public class SerializerBean implements Serializable {
    private String test1;
    private String test2;

    public SerializerBean() {
        this.test1 = "111";
        this.test2 = "222";
    }
    public String getTest1() {
        return test1;
    }

    public void setTest1(String test1) {
        this.test1 = test1;
    }

    public String getTest2() {
        return test2;
    }

    public void setTest2(String test2) {
        this.test2 = test2;
    }
}
