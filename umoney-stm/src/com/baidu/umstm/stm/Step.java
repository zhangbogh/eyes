package com.baidu.umstm.stm;

import java.util.ArrayList;
import java.util.List;

/**
 * 无状态，不允许保存私有属性 开发人员主要重载该类的实现
 * 
 * step内不允许保存stm，step是无状态的
 * 
 * @author baidu
 *
 */
public abstract class Step {
    protected int stepNo;// 第几步
    List<StatusGo> gos; // 节点流出，不同状态码流向

    public Step(int stepNo, String stepName) {
        this.stepNo = stepNo;
        this.gos = new ArrayList<StatusGo>();
    }

    /**
     * 正常流转入口逻辑通过实现该方法实现
     * 
     */
    public abstract void comeIn(StateMachine<?> stm);

    /**
     * 暂停恢复特殊处理的通过实现该方法实现，step外被动调用
     */
    public abstract void resume(StateMachine<?> stm);

    /**
     * 出口逻辑通过添加线条和路径实现
     * 
     * status = -1表示不判断状态
     * 
     * @param status
     *            >0
     * @param toStep
     * @param lineNo
     */
    public void addGo(int triggerStatus, int toStep) {
        gos.add(new StatusGo(stepNo, toStep, triggerStatus));
    }

    public void addLineDataValidator(int lineNo, String jsonValidator) {

    }

    public <T> T getLineDataVo(int lineNo, Class<T> clz) {
        return null;
    }

    class StatusGo {
        int triggerStatus;
        int toStep;
        int fromStep;

        public StatusGo(int fs, int ts, int s) {
            this.fromStep = fs;
            this.toStep = ts;
            this.triggerStatus = s;
        }
    }

}
