package com.baidu.umstm.model;

public class BizFlow {
    // 流水号
    long serialno;
    // 用户id
    int bid;
    // 流程类型：0 审核流 1 业务流
    int type;
    // 流程id
    int flowid;
    // 当前步骤
    int curstep;
    // 当前状态
    int curstatus;
    // 最终状态
    int finalstatus;

    public long getSerialno() {
        return serialno;
    }

    public void setSerialno(long serial) {
        this.serialno = serial;
    }

    public int getBid() {
        return bid;
    }

    public void setBid(int bid) {
        this.bid = bid;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getFlowid() {
        return flowid;
    }

    public void setFlowid(int flowid) {
        this.flowid = flowid;
    }

    public int getCurstatus() {
        return curstatus;
    }

    public void setCurstatus(int curstatus) {
        this.curstatus = curstatus;
    }

    public int getCurstep() {
        return curstep;
    }

    public void setCurstep(int curstep) {
        this.curstep = curstep;
    }

    public int getFinalstatus() {
        return finalstatus;
    }

    public void setFinalstatus(int finalstatus) {
        this.finalstatus = finalstatus;
    }
}
