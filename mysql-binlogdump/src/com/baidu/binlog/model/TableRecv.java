package com.baidu.binlog.model;

import java.util.List;

public class TableRecv {
    String tablename;
    int tableno;
    List<String> insertsql;
    List<String> updatesql;
    List<String> deletesql;

    public String getTablename() {
        return tablename;
    }

    public void setTablename(String tablename) {
        this.tablename = tablename;
    }

    public int getTableno() {
        return tableno;
    }

    public void setTableno(int tableno) {
        this.tableno = tableno;
    }

    public List<String> getInsertsql() {
        return insertsql;
    }

    public void setInsertsql(List<String> insertsql) {
        this.insertsql = insertsql;
    }

    public List<String> getUpdatesql() {
        return updatesql;
    }

    public void setUpdatesql(List<String> updatesql) {
        this.updatesql = updatesql;
    }

    public List<String> getDeletesql() {
        return deletesql;
    }

    public void setDeletesql(List<String> deletesql) {
        this.deletesql = deletesql;
    }

}
