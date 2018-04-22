package oom;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ruanxin
 * @create 2018-04-21
 * @desc
 */
public class OomTest {

    public static void main(String[] args) {
        int limit = 1000000000;
        Map<Integer, Integer> map = new HashMap<>(100000000);
        for (int i = 0; i < limit;i++) {
            map.put(i, i);
        }
    }
}
