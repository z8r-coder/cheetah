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
public class CheetahMap implements Map<String, ExpEntryValue> {

    private int byteSize;
    private Map<String, ExpEntryValue> map;

    public CheetahMap () {
        byteSize = 0;
        map = new ConcurrentHashMap<>();
    }

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
    public ExpEntryValue get(Object key) {
        return map.get(key);
    }

    @Override
    public ExpEntryValue put(String key, ExpEntryValue value) {
        return map.put(key, value);
    }

    @Override
    public ExpEntryValue remove(Object key) {
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
    public Set<Entry<String, ExpEntryValue>> entrySet() {
        return map.entrySet();
    }


    public int getByteSize() {
        return byteSize;
    }
}
