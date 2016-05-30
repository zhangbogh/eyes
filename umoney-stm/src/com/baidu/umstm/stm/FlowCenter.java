package com.baidu.umstm.stm;

import java.util.HashMap;
import java.util.List;

/**
 * 流程中心，所有的流程必须来这里注册
 * 
 * @author baidu
 *
 */
public class FlowCenter {
    static HashMap<Integer, List<Step>> flowMap;

    static {
        // 没有线程安全的问题，因为流程都是预先固定死的
        flowMap = new HashMap<Integer, List<Step>>();
    }

    /**
     * 注册流
     * 
     * @param flowid
     * @param steps
     */
    public static void regFlow(int flowid, List<Step> steps) {
        flowMap.put(flowid, steps);
    }

    public static List<Step> getFlow(int flowid) {
        return flowMap.get(flowid);
    }
}
