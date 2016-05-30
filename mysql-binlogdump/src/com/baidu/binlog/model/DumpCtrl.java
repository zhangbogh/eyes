package com.baidu.binlog.model;

public class DumpCtrl {
    // 单位s
    int time;
    int maxrecord;
    String fileprefix;
    String binlogfile;
    int binlogpos;

    public String getBinlogfile() {
        return binlogfile;
    }

    public void setBinlogfile(String binlogfile) {
        this.binlogfile = binlogfile;
    }

    public int getBinlogpos() {
        return binlogpos;
    }

    public void setBinlogpos(int binlogpos) {
        this.binlogpos = binlogpos;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getMaxrecord() {
        return maxrecord;
    }

    public void setMaxrecord(int maxrecord) {
        this.maxrecord = maxrecord;
    }

    public String getFileprefix() {
        return fileprefix;
    }

    public void setFileprefix(String fileprefix) {
        this.fileprefix = fileprefix;
    }

}
