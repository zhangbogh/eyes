package com.baidu.binlog.model;

public class TableDump {
    String tablename;
    String tableno;
    int idfieldno;
    String dumpsql;

    public String getTablename() {
        return tablename;
    }

    public void setTablename(String tablename) {
        this.tablename = tablename;
    }

    public String getTableno() {
        return tableno;
    }

    public void setTableno(String tableno) {
        this.tableno = tableno;
    }

    public int getIdfieldno() {
        return idfieldno;
    }

    public void setIdfieldno(int idfieldno) {
        this.idfieldno = idfieldno;
    }

    public String getDumpsql() {
        return dumpsql;
    }

    public void setDumpsql(String dumpsql) {
        this.dumpsql = dumpsql;
    }

}
