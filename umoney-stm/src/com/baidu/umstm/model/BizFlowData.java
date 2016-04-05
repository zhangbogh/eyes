package com.baidu.umstm.model;

import java.util.ArrayList;
import java.util.List;

public class BizFlowData<T> {
    // 记录用户自定义的数据
    T cusData;

    // 记录状态机每步流转的状态
    List<StepStatus> sss;

    public BizFlowData() {
    }

    public BizFlowData(T da) {
        this.cusData = da;
        sss = new ArrayList<StepStatus>();
    }

    public void addStepStatus(int stepNo, int status, String message) {
        sss.add(new StepStatus(stepNo, status, message));
    }

    public T getCusData() {
        return cusData;
    }
}
