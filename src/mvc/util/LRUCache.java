package mvc.util;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 线程安全的 LRU 缓存。
 *
 * <p>实现思路：{@link LinkedHashMap} 的构造函数第三个参数 accessOrder 传 true 后，
 * 每次 get / put 都会把被访问的节点移到链表尾部；再重写 removeEldestEntry，
 * 让元素数超过容量时自动淘汰链表头部（也就是最久未使用）的那个节点。
 * 这样增删查都保持在 O(1)。
 *
 * <p>内置命中率统计，便于观测缓存收益。
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
