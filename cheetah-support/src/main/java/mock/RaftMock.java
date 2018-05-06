package mock;

import constants.Globle;
import utils.Configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author ruanxin
 * @create 2018-04-17
 * @desc raft mock class
 */
public class RaftMock {

    public static Map<Integer, String> rootPathMapping = new HashMap<>();
    private static Configuration configuration = new Configuration();

    static {
        rootPathMapping.put(6060, configuration.getRaftTestRootPathA());
        rootPathMapping.put(7070, configuration.getRaftTestRootPathB());
        rootPathMapping.put(8080, configuration.getRaftTestRootPathC());
        rootPathMapping.put(9090, configuration.getRaftTestRootPathD());
    }

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
