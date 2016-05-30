package com.baidu.syncpara.cfg;

public class RedisPool {
    String ip;
    int port;
    int maxactive;
    int maxwait;
    int maxidel;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getMaxactive() {
        return maxactive;
    }

    public void setMaxactive(int maxactive) {
        this.maxactive = maxactive;
    }

    public int getMaxwait() {
        return maxwait;
    }

    public void setMaxwait(int maxwait) {
        this.maxwait = maxwait;
    }

    public int getMaxidel() {
        return maxidel;
    }

    public void setMaxidel(int maxidel) {
        this.maxidel = maxidel;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
