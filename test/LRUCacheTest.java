import mvc.util.LRUCache;

public class LRUCacheTest {
    static int pass = 0, fail = 0;

    static void check(String name, boolean cond) {
        if (cond) { pass++; System.out.println("  PASS  " + name); }
        else { fail++; System.out.println("  FAIL  " + name); }
    }

    public static void main(String[] args) {
        LRUCache<String, Integer> c = new LRUCache<>(3);

        c.put("a", 1);
        c.put("b", 2);
        c.put("c", 3);
        check("容量内不淘汰", c.size() == 3);

        c.get("a");
        c.put("d", 4);
        check("淘汰最久未使用 b", c.get("b") == null);
        check("保留刚访问的 a", c.get("a") == 1);
        check("淘汰后大小为 3", c.size() == 3);

        long hitBefore = c.getHitCount();
        long missBefore = c.getMissCount();
        c.get("a");
        c.get("zzz");
        check("命中计数 +1", c.getHitCount() == hitBefore + 1);
        check("未命中计数 +1", c.getMissCount() == missBefore + 1);

        c.invalidate("a");
        check("invalidate 生效", c.get("a") == null);

        c.clear();
        check("clear 后 size 归零", c.size() == 0 && c.getHitCount() == 0 && c.getMissCount() == 0);

        c.put(null, 1);
        check("拒绝 null key", c.size() == 0);

        LRUCache<Integer, Integer> big = new LRUCache<>(100);
        long t0 = System.nanoTime();
        for (int i = 0; i < 200000; i++) {
            big.put(i, i);
            big.get(i - 50);
        }
        long ms = (System.nanoTime() - t0) / 1_000_000;
        check("20 万次写读容量始终不超 100", big.size() == 100);

        System.out.println("\n淘汰行为耗时 " + ms + " ms（200000 put + 200000 get）");
        System.out.println(big.stats());
        System.out.println("\n总计: " + pass + " 通过, " + fail + " 失败");
        if (fail > 0) System.exit(1);
    }
}
