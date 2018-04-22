package hash;

import org.apache.log4j.Logger;
import utils.DateUtil;

import java.util.Map;

/**
 * @author ruanxin
 * @create 2018-04-22
 * @desc kv engine
 */
public class MapProxy {

    private Logger logger = Logger.getLogger(MapProxy.class);

    private CheetahMap delegateMap;

    public MapProxy () {
        this.delegateMap = new CheetahMap();
    }

    public MapProxy (int initCap) {
        this.delegateMap = new CheetahMap(initCap);
    }

    public MapProxy (CheetahMap delegateMap) {
        this.delegateMap = delegateMap;
    }

    /**
     * set data forever
     * @param key
     * @param data
     */
    public boolean set(String key, byte[] data) {
        try {
            ExpEntryValue expEntryValue = new ExpEntryValue(null, data);
            delegateMap.put(key, expEntryValue);
            return true;
        } catch (Exception ex) {
            logger.error("cheetah hash set kv without exp time occurs error:",ex);
            return false;
        }

    }

    /**
     * time unit m, default
     * @param key
     * @param data
     * @param expTime
     */
    public boolean set(String key, byte[] data, int expTime) {
        return set(key, data, expTime, DateUtil.TimeUnit.mm);
    }

    /**
     * set kv with exp
     * @param key
     * @param data
     * @param expTime
     * @param timeUnit
     */
    public boolean set(String key, byte[] data, int expTime, DateUtil.TimeUnit timeUnit) {
        try {
            String exp = DateUtil.convertDateToStr(DateUtil.add(timeUnit, expTime), DateUtil.DEFAULT_PAY_FORMAT);
            ExpEntryValue expEntryValue = new ExpEntryValue(exp, data);
            delegateMap.put(key, expEntryValue);
            return true;
        } catch (Exception ex) {
            logger.error("cheetah hash set kv with exp time occurs error:",ex);
            return false;
        }
    }

    public byte[] get(String key) {
        ExpEntryValue value = delegateMap.get(key);
        if (value == null) {
            return null;
        }
        String expTime = value.getExpTime();
        if (!DateUtil.compareTime(expTime)) {
            //exp
            delegateMap.remove(key);
            return null;
        }
        //no exp
        return value.getData();
    }

    public String getKvExpTime (String key) {
        ExpEntryValue value = delegateMap.get(key);
        if (value == null) {
            return null;
        }
        return value.getExpTime();
    }

    public byte[] remove (String key) {
        ExpEntryValue value = delegateMap.remove(key);
        if (value == null) {
            return null;
        }
        return value.getData();
    }

    /**
     * kv count
     * @return
     */
    public int size () {
        return delegateMap.size();
    }

    /**
     * value size
     * @return
     */
    public long byteSize () {
        return delegateMap.getByteSize();
    }

    public static void main(String[] args) {
        MapProxy proxy = new MapProxy();
        proxy.set("test", "test".getBytes(), 5, DateUtil.TimeUnit.ss);
        System.out.println(proxy.get("test"));
    }
}
