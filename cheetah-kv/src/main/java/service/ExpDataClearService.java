package service;

import hash.MapProxy;
import org.apache.log4j.Logger;

/**
 * @author ruanxin
 * @create 2018-04-22
 * @desc 过期数据定时清除
 */
public class ExpDataClearService {

    private final static Logger logger = Logger.getLogger(ExpDataClearService.class);

    private MapProxy mapProxy;

    public ExpDataClearService (MapProxy mapProxy) {
        this.mapProxy = mapProxy;
    }

    public void doTask () {
        for (String key : mapProxy.keySet()) {
            mapProxy.get(key);
        }
    }

    public MapProxy getMapProxy() {
        return mapProxy;
    }

    public void setMapProxy(MapProxy mapProxy) {
        this.mapProxy = mapProxy;
    }
}
