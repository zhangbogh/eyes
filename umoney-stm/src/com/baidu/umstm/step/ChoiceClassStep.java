package com.baidu.umstm.step;

import com.baidu.umstm.stm.StateMachine;
import com.baidu.umstm.stm.Step;
import com.baidu.umstm.util.JsonValidator;

public class ChoiceClassStep extends Step {
    public final static int STATUS_CLASSFULL = 0;
    public final static int STATUS_PASS = 1;
    public final static int STATUS_JSONCHECKFAIL = 2;

    public ChoiceClassStep(int stepNo, String n) {
        super(stepNo, "选课");
    }

    @Override
    public void comeIn(StateMachine<?> stm) {
        String json = stm.getBizFlowData().getStepJsonData(stepNo);
        JsonValidator jv = new JsonValidator("classchoice");

    }

    @Override
    public void resume(StateMachine<?> stm) {

    }

}
