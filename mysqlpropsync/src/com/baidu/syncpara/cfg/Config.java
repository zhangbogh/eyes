package com.baidu.syncpara.cfg;

public class Config {
    String codetpl;
    String classname;
    String tablename;

    DataBase dbsrc;
    RedisPool redispool;

    public String getCodetpl() {
        return codetpl;
    }

    public void setCodetpl(String codetpl) {
        this.codetpl = codetpl;
    }

    public String getClassname() {
        return classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }

    public String getTablename() {
        return tablename;
    }

    public void setTablename(String tablename) {
        this.tablename = tablename;
    }

    public DataBase getDbsrc() {
        return dbsrc;
    }

    public void setDbsrc(DataBase dbsrc) {
        this.dbsrc = dbsrc;
    }

    public RedisPool getRedispool() {
        return redispool;
    }

    public void setRedispool(RedisPool redispool) {
        this.redispool = redispool;
    }

}
