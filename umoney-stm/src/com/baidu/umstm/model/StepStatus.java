package com.baidu.umstm.model;

public class StepStatus {
    int stepNo;
    int status;
    String message;

    public StepStatus(int sn, int sa, String msg) {
        this.stepNo = sn;
        this.status = sa;
        this.message = msg;
    }

    public int getStepNo() {
        return stepNo;
    }

    public void setStepNo(int stepNo) {
        this.stepNo = stepNo;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
