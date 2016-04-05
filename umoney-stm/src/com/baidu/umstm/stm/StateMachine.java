package com.baidu.umstm.stm;

import com.baidu.umstm.model.BizFlow;
import com.baidu.umstm.node.Step;

import java.util.List;

public class StateMachine {
    List<Step> steps;

    BizFlow bf;

    /**
     * 新启动流程
     * 
     * @param flowid
     */
    public StateMachine(int flowid) {
        bf = new BizFlow();
        bf.setFlowid(flowid);
        steps = FlowCenter.getFlow(flowid);
    }

    /**
     * 启动已有流程
     * 
     * @param flowid
     */
    public StateMachine(long serialNo) {
        bf = getBizFlow(serialNo);
        steps = FlowCenter.getFlow(bf.getFlowid());
    }

    private BizFlow getBizFlow(long serialNo) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * 状态流转
     */
    public void next() {

    }

    public void updateStatusNext() {

    }

    public void persistStatusPause() {

    }

    public void persistStatusEnd() {

    }
}
