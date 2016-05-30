package com.baidu.umstm.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BizFlowData {
    // 记录状态机每步流转的状态
    List<StepStatus> sss;

    // 每个步骤对应的数据
    HashMap<Integer, String> stepJsonData;

    public BizFlowData() {
        sss = new ArrayList<StepStatus>();
        stepJsonData = new HashMap<Integer, String>();
    }

    public void addStepStatus(int stepNo, int status, String message) {
        sss.add(new StepStatus(stepNo, status, message));
    }

    public List<StepStatus> getStepStatue() {
        return sss;
    }

    public String getStepJsonData(int stepNo) {
        return stepJsonData.get(stepNo);
    }

    public void putStepJsonData(int stepNo, String json) {
        stepJsonData.put(stepNo, json);
    }
}
