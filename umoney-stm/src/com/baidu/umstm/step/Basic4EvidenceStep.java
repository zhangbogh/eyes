package com.baidu.umstm.step;

import com.baidu.umstm.model.aps.Basic4EvidenceApsReq;
import com.baidu.umstm.model.php.Basic4EvidencePhpReq;
import com.baidu.umstm.stm.StateMachine;
import com.baidu.umstm.stm.Step;
import com.baidu.umstm.util.JsonValidator;
import com.baidu.umstm.util.ObjConvertor;

import com.google.gson.Gson;

public class Basic4EvidenceStep extends Step {
    public final static int STATUS_JSONCHECKFAIL = 0;
    public final static int STATUS_APSCHECKFAIL = 1;
    public final static int STATUS_APSPASS = 2;

    public Basic4EvidenceStep(int stepNo,String n) {
        super(stepNo,"四项基本验证");
    }

    @Override
    public void comeIn(StateMachine<?> stm) {
        String json = stm.getBizFlowData().getStepJsonData(stepNo);
        JsonValidator jv = new JsonValidator("basic4evidence");
        if (jv.check(json)) {
            Basic4EvidencePhpReq b4ep = new Gson().fromJson(json, Basic4EvidencePhpReq.class);
            ObjConvertor oc = new ObjConvertor();
            Basic4EvidenceApsReq b4ea = oc.conver2Clz(b4ep, Basic4EvidenceApsReq.class);
            // call aps
            boolean result = false;
            if (result == true) {
                stm.updateStatus(stepNo, STATUS_APSPASS, "");
                stm.next();
            } else {
                stm.updateStatus(stepNo, STATUS_APSCHECKFAIL, "aps 风控原因");
                stm.pause();
                return;
            }
        } else {
            stm.updateStatus(stepNo, STATUS_JSONCHECKFAIL, jv.getCheckResult());
            stm.pause();
            return;
        }
    }

    @Override
    public void resume(StateMachine<?> stm) {
        int status = stm.getBizFlow().getCurstatus();
        switch (status) {
        case STATUS_APSCHECKFAIL:
            comeIn(stm);
            break;
        case STATUS_JSONCHECKFAIL:
            comeIn(stm);
            break;
        default:
            break;
        }

    }

}
