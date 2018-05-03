package hash;

import org.apache.log4j.Logger;
import service.ExpDataClearService;
import task.TaskCenter;
import utils.DateUtil;

import java.util.Collection;
import java.util.Map;
import java.util.Set;


/**
 * @author ruanxin
 * @create 2018-04-22
 * @desc kv engine
 */
public class MapProxy {

    private Logger logger = Logger.getLogger(MapProxy.class);

    private CheetahMap delegateMap;
    private TaskCenter taskCenter = TaskCenter.getInstance();;
    private ExpDataClearService expDataClearService = new ExpDataClearService(this);

    public MapProxy () {
        this.delegateMap = new CheetahMap();
        taskCenter.startExpDataClearTask(expDataClearService);
    }

    public MapProxy (int initCap) {
        this.delegateMap = new CheetahMap(initCap);
        taskCenter.startExpDataClearTask(expDataClearService);
    }

    public MapProxy (CheetahMap delegateMap) {
        this.delegateMap = delegateMap;
        taskCenter.startExpDataClearTask(expDataClearService);
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

    /**
     * get and clear exp data
     * @param key
     * @return
     */
    public byte[] get(String key) {
        ExpEntryValue value = delegateMap.get(key);
        if (value == null) {
            return null;
        }
        String expTime = value.getExpTime();
        if (expTime == null) {
            return value.getData();
        }
        if (!DateUtil.compareTime(expTime)) {
            //exp
            delegateMap.remove(key);
            return null;
        }
        //no exp
        return value.getData();
    }

    /**
     * get exp time
     * @param key
     * @return
     */
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

    public Collection values() {
        return delegateMap.values();
    }

    public Set<String> keySet () {
        return delegateMap.keySet();
    }
    public Set<Map.Entry<String, ExpEntryValue>> entrySet() {
        return delegateMap.entrySet();
    }

    public static void main(String[] args) {
        MapProxy proxy = new MapProxy();
        proxy.set("test", "test".getBytes(), 5, DateUtil.TimeUnit.ss);
        for (int i = 0; i < 10;i++) {
            proxy.set(i + "t", "test".getBytes());
        }
        for (String key : proxy.keySet()) {
            if (key.equals("5t") || key.equals("6t")) {
                proxy.remove(key);
            }
        }
        for (String key : proxy.keySet()) {
            System.out.println(key);
        }
//        try {
//            Thread.sleep(4000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        byte[] data = proxy.get("test");
//        if (data == null) {
//            System.out.println("null");
//        } else {
//            String str = new String(data);
//            System.out.println(str);
//        }
    }
}
