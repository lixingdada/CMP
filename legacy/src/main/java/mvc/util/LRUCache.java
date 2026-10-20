package mvc.util;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 线程安全的 LRU 缓存。
 *
 * <p>LinkedHashMap 构造时 accessOrder 传 true，get/put 会把节点挪到链表尾部，
 * 再重写 removeEldestEntry 在超容量时淘汰头部最久未用的那个。
 * 顺带记了命中次数，方便看缓存到底有没有用。
 */
public class LRUCache<K, V> {

    private final int capacity;
    private final LinkedHashMap<K, V> map;

    private long hitCount;
    private long missCount;

    public LRUCache(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity must be positive");
        }
        this.capacity = capacity;
        this.map = new LinkedHashMap<K, V>(16, 0.75f, true) {
            private static final long serialVersionUID = 1L;

            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() > LRUCache.this.capacity;
            }
        };
    }

    public synchronized V get(K key) {
        V value = map.get(key);
        if (value == null) {
            missCount++;
        } else {
            hitCount++;
        }
        return value;
    }

    public synchronized void put(K key, V value) {
        if (key == null || value == null) {
            return;
        }
        map.put(key, value);
    }

    public synchronized void invalidate(K key) {
        map.remove(key);
    }

    public synchronized void clear() {
        map.clear();
        hitCount = 0;
        missCount = 0;
    }

    public synchronized int size() {
        return map.size();
    }

    public synchronized int getCapacity() {
        return capacity;
    }

    public synchronized long getHitCount() {
        return hitCount;
    }

    public synchronized long getMissCount() {
        return missCount;
    }

    /** 命中率，0~1 */
    public synchronized double hitRate() {
        long total = hitCount + missCount;
        return total == 0 ? 0D : (double) hitCount / total;
    }

    public synchronized String stats() {
        return "LRUCache{capacity=" + capacity
                + ", size=" + map.size()
                + ", hit=" + hitCount
                + ", miss=" + missCount
                + ", hitRate=" + String.format("%.2f%%", hitRate() * 100) + "}";
    }
}
