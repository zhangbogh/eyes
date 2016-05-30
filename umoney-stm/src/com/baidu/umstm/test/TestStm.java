package com.baidu.umstm.test;

import com.baidu.umstm.step.Basic4EvidenceStep;
import com.baidu.umstm.step.ChoiceClassStep;
import com.baidu.umstm.stm.FlowCenter;
import com.baidu.umstm.stm.Step;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

public class TestStm extends TestCase {
    public void testBasic() {
        List<Step> flow1 = new ArrayList<Step>();
        Basic4EvidenceStep step1 = new Basic4EvidenceStep(1,"四项");
        ChoiceClassStep step2 = new ChoiceClassStep(2,"选课");
        step1.addGo(Basic4EvidenceStep.STATUS_APSPASS, 2);
        flow1.add(step1);
        flow1.add(step2);
        FlowCenter.regFlow(1, flow1);
    }
}
