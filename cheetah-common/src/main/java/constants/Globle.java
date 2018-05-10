package constants;

import java.util.concurrent.TimeUnit;

/**
 * @author ruanxin
 * @create 2018-02-12
 * @desc
 */
public class Globle {
    //real
    public final static int REG_HEART_BEAT_INIT = 40;
    public final static int REG_HEART_BEAT_INTERVAL = 40;
    public final static int REG_UPDATE_SERVER_LIST = 45;
    public final static String SYNC_SIGNAL = "synchronization";
    public final static String ASYNC_SIGNAL = "asynchronous";

    //test
    public final static int REG_HEART_BEAT_INIT_TEST = 3;
    public final static int REG_HEART_BEAT_INTERVAL_TEST = 3;
    public final static int REG_UPDATE_SERVER_LIST_TEST = 5;
    public final static String localHost = "127.0.0.1";
    public final static int localPortTest1 = 4332;
    public final static int localPortTest2 = 4333;

    public final static int electionTimeOut = 1;
    public final static int electionTimeOutRandom = 6;

    public final static TimeUnit testTimeUnit = TimeUnit.SECONDS;

}
