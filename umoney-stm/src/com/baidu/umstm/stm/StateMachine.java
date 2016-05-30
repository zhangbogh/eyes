package com.baidu.umstm.stm;

import com.baidu.umstm.model.BizFlow;
import com.baidu.umstm.model.BizFlowData;
import com.baidu.umstm.stm.Step.StatusGo;

import java.util.HashMap;
import java.util.List;

/**
 * 有状态，每次会产生新的对象 非线程安全的 <br>
 * 用户数据注入到这个层次<br>
 * 
 * 对于step来说只看到这个对象
 * 
 * @author baidu
 *
 */
public class StateMachine<T> {
    // 流程对应的步骤
    HashMap<Integer, Step> stepMap = new HashMap<Integer, Step>();
    // 流程对应的状态对象
    BizFlow bf;
    // 流程相关的数据对象
    BizFlowData bfd;

    /**
     * 新启动流程
     * 
     * @param flowid
     */
    public StateMachine(int flowid) {
        bf = new BizFlow();
        bf.setFlowid(flowid);
        // TODO:插入数据库产生新的serialNo
        List<Step> steps = FlowCenter.getFlow(flowid);
        for (Step s : steps) {
            stepMap.put(s.stepNo, s);
        }
    }

    /**
     * 启动已有流程
     * 
     * @param flowid
     */
    public StateMachine(long serialNo) {
        bf = getBizFlow(serialNo);
        List<Step> steps = FlowCenter.getFlow(bf.getFlowid());
        for (Step s : steps) {
            stepMap.put(s.stepNo, s);
        }
    }

    private BizFlow getBizFlow(long serialNo) {
        // TODO Auto-generated method stub
        return null;
    }

    public BizFlow getBizFlow() {
        return bf;
    }

    public BizFlowData getBizFlowData() {
        return bfd;
    }

    public long getSerialNo() {
        return bf.getSerialno();
    }

    /**
     * 内存更新状态
     * 
     * @param stepNo
     * @param status
     * @param msg
     * @return
     */
    public StateMachine<T> updateStatus(int stepNo, int status, String msg) {
        return this;
    }

    /**
     * 内存和数据库都更新状态
     * 
     * @param stepNo
     * @param status
     * @param msg
     * @return
     */
    public StateMachine<T> persistStatus(int stepNo, int status, String msg) {
        return this;
    }

    /**
     * 驱动状态机运转,会导致程序压栈的行为,状态机不能太长
     */
    public void next() {
        // TODO:判断流程是否结束，如果已结束直接返回
        if (bf.getFinalstatus() > 0) {
            return;
        }

        Step s = stepMap.get(bf.getCurstep());
        boolean finded = false;
        for (StatusGo go : s.gos) {
            if (go.triggerStatus == bf.getCurstatus()) {
                Step to = stepMap.get(go.toStep);
                // TODO:调整bizflow的curstatus
                to.comeIn(this);
                finded = true;
                break;
            }
        }

        if (!finded) {
            // TODO:丢异常上去
        }
    }

    /**
     * 暂停流程：通常用于异步调用外部系统
     */
    public void pause() {
        return;
    }

    /**
     * 重启流程：通常用于异步调用外部系统
     */
    public void resume() {
        return;
    }

    /**
     * 结束流程
     */
    public void end(int status) {
        bf.setFinalstatus(status);
        // TODO：更新数据库最终状态

    }

    /**
     * 启动流程
     */
    public void begin() {
        // 0号步骤是流程启动入口
        Step s = stepMap.get(0);
        s.comeIn(this);
    }
}
