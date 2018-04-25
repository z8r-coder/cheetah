package mock;

import constants.Globle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author ruanxin
 * @create 2018-04-17
 * @desc raft mock class
 */
public class RaftMock {

    /**
     * 获取选举超时时间
     * @return
     */
    public static int getElectionTimeOutMs () {
        Random random = new Random();
        int randomTimeOutMs = Globle.electionTimeOut
                + random.nextInt(Globle.electionTimeOutRandom);
        return randomTimeOutMs;
    }

    public static TimeUnit getRaftMockTimeUnit () {
        return Globle.testTimeUnit;
    }
}
