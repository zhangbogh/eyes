package com.baidu.binlog.dump;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 非线程安全
 * 
 * @author zhangbo07
 *
 * @param <K>
 * @param <V>
 */
public class LRUCache<K, V> extends LinkedHashMap<K, V> {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private final int MAX_CACHE_SIZE;

    public LRUCache(int cacheSize) {
        super((int) Math.ceil(cacheSize / 0.75) + 1, 0.75f, true);
        MAX_CACHE_SIZE = cacheSize;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > MAX_CACHE_SIZE;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<K, V> entry : entrySet()) {
            sb.append(String.format("{}:{} ", entry.getKey(), entry.getValue()));
        }
        return sb.toString();
    }
}
