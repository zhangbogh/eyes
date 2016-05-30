package com.baidu.binlog.dump;

/**
 * 非线程安全
 * 
 * @author zhangbo07
 *
 */
public class TableCache {
    LRUCache<Long, String> tableMap;

    public TableCache(int maxNum) {
        tableMap = new LRUCache<Long, String>(maxNum);
    }

    public void putTable(Long id, String name) {
        tableMap.put(id, name);
    }

    public String getTable(Long id) {
        return tableMap.get(id);
    }
}
