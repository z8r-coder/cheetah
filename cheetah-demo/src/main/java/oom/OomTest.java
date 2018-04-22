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
        String value = "abcadsadsadsadsadsadsadsadsadsadsadsadsadas";
        Map<Integer, String> map = new HashMap<>(1000000000);
        for (int i = 0; i < 2;i++) {
            System.out.println(i);
            map.put(i, value);
        }
    }
}
