package utils;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author ruanxin
 * @create 2018-02-09
 * @desc
 */
public class Configuration {
    private final static Logger logger = Logger.getLogger(Configuration.class);

    private String registerHost;
    private int registerPort;
    private String cheetahSignal;
    private String raftLogPath;
    private String raftMetaPath;
    private String raftRootPath;
    private String raftInitServer;
    private String raftClusterHost;
    private String raftClusterPort;

    //    -------------------   test ----------------
    private String raftTestLogPathA;
    private String raftTestMetaPathA;
    private String raftTestRootPathA;

    private String raftTestLogPathB;
    private String raftTestMetaPathB;
    private String raftTestRootPathB;

    private String raftTestLogPathC;
    private String raftTestMetaPathC;
    private String raftTestRootPathC;

    private String raftTestLogPathD;
    private String raftTestMetaPathD;
    private String raftTestRootPathD;

    private Properties properties;

    public Configuration () {
        loadPropertiesFromSrc();
    }

    public void loadPropertiesFromSrc() {
        InputStream in = null;

        try {
            in = Configuration.class.getClassLoader().getResourceAsStream("env.properties");
            if (in != null) {
                this.properties = new Properties();
                this.properties.load(in);
            }
            loadProperties(properties);
        } catch (Exception e) {
            logger.error("Read properties Error", e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private void loadProperties (Properties properties) {
        String port = properties.getProperty("register.port");
        if (!StringUtils.isBlank(port)) {
            this.registerPort = Integer.parseInt(port.trim());
        }
        String host = properties.getProperty("register.host");
        if (!StringUtils.isBlank(host)) {
            this.registerHost = host.trim();
        }
        String signal = properties.getProperty("cheetah.signal");
        if (!StringUtils.isBlank(signal)) {
            this.cheetahSignal = signal.trim();
        }
        String raftLogPath = properties.getProperty("raft.log.path");
        if (!StringUtils.isBlank(raftLogPath)) {
            this.raftLogPath = raftLogPath;
        }
        String raftMetaPath = properties.getProperty("raft.meta.path");
        if (!StringUtils.isBlank(raftMetaPath)) {
            this.raftMetaPath = raftMetaPath;
        }
        String raftRootPath = properties.getProperty("raft.root.path");
        if (!StringUtils.isBlank(raftRootPath)) {
            this.raftRootPath = raftRootPath;
        }
        String raftInitServer = properties.getProperty("raft.init.server");
        if (!StringUtils.isBlank(raftInitServer)) {
            this.raftInitServer = raftInitServer;
        }
        String raftClusterHost = properties.getProperty("raft.client.cluster.host");
        if (!StringUtils.isBlank(raftClusterHost)) {
            this.raftClusterHost = raftClusterHost;
        }
        String raftClusterPort = properties.getProperty("raft.client.cluster.port");
        if (!StringUtils.isBlank(raftClusterPort)) {
            this.raftClusterPort = raftClusterPort;
        }

        //--------------------   test data -------------------------
        String raftTestLogPathA = properties.getProperty("raft.test.log.path_A");
        if (!StringUtils.isBlank(raftTestLogPathA)) {
            this.raftTestLogPathA = raftTestLogPathA;
        }
        String raftTestMetaPathA = properties.getProperty("raft_test.meta.path_A");
        if (!StringUtils.isBlank(raftTestMetaPathA)) {
            this.raftTestMetaPathA = raftTestMetaPathA;
        }
        String raftTestRootPathA = properties.getProperty("raft.test.root.path_A");
        if (!StringUtils.isBlank(raftTestRootPathA)) {
            this.raftTestRootPathA = raftTestRootPathA;
        }

        String raftTestLogPathB = properties.getProperty("raft.test.log.path_B");
        if (!StringUtils.isBlank(raftTestLogPathB)) {
            this.raftTestLogPathB = raftTestLogPathB;
        }
        String raftTestMetaPathB = properties.getProperty("raft_test.meta.path_B");
        if (!StringUtils.isBlank(raftTestMetaPathB)) {
            this.raftTestMetaPathB = raftTestMetaPathB;
        }
        String raftTestRootPathB = properties.getProperty("raft.test.root.path_B");
        if (!StringUtils.isBlank(raftTestRootPathB)) {
            this.raftTestRootPathB = raftTestRootPathB;
        }

        String raftTestLogPathC = properties.getProperty("raft.test.log.path_C");
        if (!StringUtils.isBlank(raftTestLogPathC)) {
            this.raftTestLogPathC = raftTestLogPathC;
        }
        String raftTestMetaPathC = properties.getProperty("raft_test.meta.path_C");
        if (!StringUtils.isBlank(raftTestMetaPathC)) {
            this.raftTestMetaPathC = raftTestMetaPathC;
        }
        String raftTestRootPathC = properties.getProperty("raft.test.root.path_C");
        if (!StringUtils.isBlank(raftTestRootPathC)) {
            this.raftTestRootPathC = raftTestRootPathC;
        }

        String raftTestLogPathD = properties.getProperty("raft.test.log.path_D");
        if (!StringUtils.isBlank(raftTestLogPathD)) {
            this.raftTestLogPathD = raftTestLogPathD;
        }
        String raftTestMetaPathD = properties.getProperty("raft_test.meta.path_D");
        if (!StringUtils.isBlank(raftTestMetaPathD)) {
            this.raftTestMetaPathD = raftTestMetaPathD;
        }

        String raftTestRootPathD = properties.getProperty("raft.test.root.path_D");
        if (!StringUtils.isBlank(raftTestRootPathD)) {
            this.raftTestRootPathD = raftTestRootPathD;
        }
    }

    public String getRegisterHost() {
        return registerHost;
    }

    public void setRegisterHost(String registerHost) {
        this.registerHost = registerHost;
    }

    public int getRegisterPort() {
        return registerPort;
    }

    public void setRegisterPort(int registerPort) {
        this.registerPort = registerPort;
    }

    public String getCheetahSignal() {
        return cheetahSignal;
    }

    public void setCheetahSignal(String cheetahSignal) {
        this.cheetahSignal = cheetahSignal;
    }

    public String getRaftLogPath() {
        return raftLogPath;
    }

    public void setRaftLogPath(String raftLogPath) {
        this.raftLogPath = raftLogPath;
    }

    public String getRaftMetaPath() {
        return raftMetaPath;
    }

    public void setRaftMetaPath(String raftMetaPath) {
        this.raftMetaPath = raftMetaPath;
    }

    public String getRaftRootPath() {
        return raftRootPath;
    }

    public void setRaftRootPath(String raftRootPath) {
        this.raftRootPath = raftRootPath;
    }

    public String getRaftInitServer() {
        return raftInitServer;
    }

    public void setRaftInitServer(String raftInitServer) {
        this.raftInitServer = raftInitServer;
    }

    public String getRaftClusterHost() {
        return raftClusterHost;
    }

    public void setRaftClusterHost(String raftClusterHost) {
        this.raftClusterHost = raftClusterHost;
    }

    public String getRaftClusterPort() {
        return raftClusterPort;
    }

    public void setRaftClusterPort(String raftClusterPort) {
        this.raftClusterPort = raftClusterPort;
    }

    public String getRaftTestLogPathA() {
        return raftTestLogPathA;
    }

    public void setRaftTestLogPathA(String raftTestLogPathA) {
        this.raftTestLogPathA = raftTestLogPathA;
    }

    public String getRaftTestMetaPathA() {
        return raftTestMetaPathA;
    }

    public void setRaftTestMetaPathA(String raftTestMetaPathA) {
        this.raftTestMetaPathA = raftTestMetaPathA;
    }

    public String getRaftTestRootPathA() {
        return raftTestRootPathA;
    }

    public void setRaftTestRootPathA(String raftTestRootPathA) {
        this.raftTestRootPathA = raftTestRootPathA;
    }

    public String getRaftTestLogPathB() {
        return raftTestLogPathB;
    }

    public void setRaftTestLogPathB(String raftTestLogPathB) {
        this.raftTestLogPathB = raftTestLogPathB;
    }

    public String getRaftTestMetaPathB() {
        return raftTestMetaPathB;
    }

    public void setRaftTestMetaPathB(String raftTestMetaPathB) {
        this.raftTestMetaPathB = raftTestMetaPathB;
    }

    public String getRaftTestRootPathB() {
        return raftTestRootPathB;
    }

    public void setRaftTestRootPathB(String raftTestRootPathB) {
        this.raftTestRootPathB = raftTestRootPathB;
    }

    public String getRaftTestLogPathC() {
        return raftTestLogPathC;
    }

    public void setRaftTestLogPathC(String raftTestLogPathC) {
        this.raftTestLogPathC = raftTestLogPathC;
    }

    public String getRaftTestMetaPathC() {
        return raftTestMetaPathC;
    }

    public void setRaftTestMetaPathC(String raftTestMetaPathC) {
        this.raftTestMetaPathC = raftTestMetaPathC;
    }

    public String getRaftTestRootPathC() {
        return raftTestRootPathC;
    }

    public void setRaftTestRootPathC(String raftTestRootPathC) {
        this.raftTestRootPathC = raftTestRootPathC;
    }

    public String getRaftTestLogPathD() {
        return raftTestLogPathD;
    }

    public void setRaftTestLogPathD(String raftTestLogPathD) {
        this.raftTestLogPathD = raftTestLogPathD;
    }

    public String getRaftTestMetaPathD() {
        return raftTestMetaPathD;
    }

    public void setRaftTestMetaPathD(String raftTestMetaPathD) {
        this.raftTestMetaPathD = raftTestMetaPathD;
    }

    public String getRaftTestRootPathD() {
        return raftTestRootPathD;
    }

    public void setRaftTestRootPathD(String raftTestRootPathD) {
        this.raftTestRootPathD = raftTestRootPathD;
    }

    public static void main(String[] args) {
        Configuration config = new Configuration();
        config.loadPropertiesFromSrc();
        System.out.println(config.getRaftTestLogPathA());
        System.out.println(config.getRaftTestLogPathB());
        System.out.println(config.getRaftTestLogPathC());
        System.out.println(config.getRaftTestLogPathD());

        System.out.println();

        System.out.println(config.getRaftTestMetaPathA());
        System.out.println(config.getRaftTestMetaPathB());
        System.out.println(config.getRaftTestMetaPathC());
        System.out.println(config.getRaftTestMetaPathD());

        System.out.println();

        System.out.println(config.getRaftTestRootPathA());
        System.out.println(config.getRaftTestRootPathB());
        System.out.println(config.getRaftTestRootPathC());
        System.out.println(config.getRaftTestRootPathD());
    }
}
