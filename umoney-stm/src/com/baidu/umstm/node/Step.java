package com.baidu.umstm.node;

import java.util.ArrayList;
import java.util.List;

/**
 * 无状态
 * 
 * @author baidu
 *
 */
public abstract class Step {
    int stepNo;// 第几步
    List<StatusGo> gos; // 节点流出，不同状态码流向

    public Step(int stepNo) {
        this.stepNo = stepNo;
        this.gos = new ArrayList<StatusGo>();
    }

    /**
     * 流入的线条编号，以及对端节点返回的状态码
     * 
     * @param lineNo
     * @param status
     */
    public abstract void comeIn(int lineNo, int status);

    /**
     * status = -1表示不判断状态
     * 
     * @param status
     *            >0
     * @param toStep
     * @param lineNo
     */
    public void addGo(int status, int toStep, int lineNo) {
        gos.add(new StatusGo(status, toStep));
    }

    class StatusGo {
        int curstatus;
        int toStep;

        public StatusGo(int a, int b) {
            this.curstatus = a;
            this.toStep = b;
        }
    }



}
