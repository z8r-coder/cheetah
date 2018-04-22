package hash;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ruanxin
 * @create 2018-04-22
 * @desc on heap
 */
public class CheetahMap implements Map<String, byte[]> {

    private int byteSize;
    private Map<String, byte[]> map;

    public CheetahMap (int initCap) {
        byteSize = 0;
        map = new ConcurrentHashMap<>(initCap);
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return map.containsKey(value);
    }

    @Override
    public byte[] get(Object key) {
        return map.get(key);
    }

    @Override
    public byte[] put(String key, byte[] value) {
        byteSize += value.length;
        return map.put(key, value);
    }

    @Override
    public byte[] remove(Object key) {
        return map.remove(key);
    }


    @Override
    public void putAll(Map m) {
        map.putAll(m);
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Set keySet() {
        return map.keySet();
    }

    @Override
    public Collection values() {
        return map.values();
    }

    @Override
    public Set<Entry<String, byte[]>> entrySet() {
        return map.entrySet();
    }

    public int getByteSize() {
        return byteSize;
    }
}
