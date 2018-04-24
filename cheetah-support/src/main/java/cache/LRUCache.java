package cache;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author ruanxin
 * @create 2018-04-04
 * @desc lru cache
 */
public class LRUCache<K,V> extends LinkedHashMap<K,V> {
    /**
     * cache capacity
     */
    private int capacity;

    public LRUCache() {
        super();
    }

    public LRUCache (int initCapacity, float loadFactor, boolean isLRU, int capacity) {
        super(initCapacity, loadFactor, isLRU);
        this.capacity = capacity;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        if (size() > capacity) {
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        LRUCache<Integer, Integer> lruCache = new LRUCache<>(16, 0.75f, true, 4);
        lruCache.put(1,1);
        lruCache.put(2,2);
        lruCache.put(3,3);
        lruCache.put(4,4);
        lruCache.get(1);
        lruCache.put(5,5);

        for (Integer key : lruCache.keySet()) {
            System.out.println(key);
        }

    }
}
