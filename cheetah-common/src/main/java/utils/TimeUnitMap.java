package utils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ruanxin
 * @create 2018-04-23
 * @desc
 */
public enum TimeUnitMap {
    yy("yy", DateUtil.TimeUnit.yy, "year"),
    MM("mm", DateUtil.TimeUnit.MM, "month"),
    dd("dd", DateUtil.TimeUnit.dd, "day"),
    HH("HH", DateUtil.TimeUnit.HH, "hour"),
    ss("ss", DateUtil.TimeUnit.ss, "seconds"),
    mm("mm", DateUtil.TimeUnit.mm, "minute");

    /**
     * time mapping
     */
    public static Map<String, TimeUnitMap> tuMap = new HashMap<>();
    /**
     * string
     */
    private String str;
    /**
     * time unit
     */
    private DateUtil.TimeUnit timeUnit;
    /**
     * description
     */
    private String desc;

    TimeUnitMap(String str, DateUtil.TimeUnit timeUnit, String desc) {
        this.str = str;
        this.timeUnit = timeUnit;
        this.desc = desc;
    }

    //init
    static {
        for (TimeUnitMap timeUnitMap : TimeUnitMap.values()) {
            tuMap.put(timeUnitMap.getStr(), timeUnitMap);
        }
    }

    public String getStr() {
        return str;
    }

    public void setStr(String str) {
        this.str = str;
    }

    public DateUtil.TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(DateUtil.TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
