package com.baidu.syncpara.cfg;

public class SyncParaRow {
    int id;
    int datatype;
    int version;
    int level;
    String mkey;
    String mvalue;
    int status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDatatype() {
        return datatype;
    }

    public void setDatatype(int datatype) {
        this.datatype = datatype;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getMkey() {
        return mkey;
    }

    public void setMkey(String key) {
        this.mkey = key;
    }

    public String getMvalue() {
        return mvalue;
    }

    public void setMvalue(String value) {
        this.mvalue = value;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

}
