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

    private String registerAddr;
    private String registerPort;

    private Properties properties;

    public void loadPropertiesFromSrc() {
        InputStream in = null;

        try {
            in = Configuration.class.getClassLoader().getResourceAsStream("EngineConfig.properties");
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

    }
}
