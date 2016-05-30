package com.baidu.binlog.dump;

import org.slf4j.LoggerFactory;

import org.slf4j.Logger;

import java.util.HashMap;

/**
 * 非线程安全
 * 
 * @author zhangbo07
 *
 */
public class ColumnCache {
    HashMap<String, String[]> tableColumnMap = new HashMap<String, String[]>();
    Logger log = LoggerFactory.getLogger(ColumnCache.class);

    public void refreshTableColumn(String table) {
        log.info("begin to refresh table {}", table);
        
        log.info("end to refresh table {}", table);
    }

    public String getColumnName(String table, int col) {
        if (tableColumnMap.containsKey(table)) {
            return tableColumnMap.get(table)[col];
        } else {
            log.error("can not find col index {} in table {}", col, table);
            return null;
        }
    }
}
