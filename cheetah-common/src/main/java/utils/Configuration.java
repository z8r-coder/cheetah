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

    public static void main(String[] args) {
        Configuration config = new Configuration();
        config.loadPropertiesFromSrc();
        System.out.println(config.getRaftInitServer());
    }
}
