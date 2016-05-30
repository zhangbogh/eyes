package com.union.check.checker.snapcond;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.union.check.checker.ComplexCheckCondition;
import com.union.check.obj.BeanProxy;

public class TuIncomeSumCheck implements ComplexCheckCondition {

    public static double valve = 1.0;

    @Override
    public List<BeanProxy> compare(List<List<BeanProxy>> dbs) {
        List<BeanProxy> errorList = new ArrayList<BeanProxy>();

        if (dbs == null || dbs.size() != 2 || dbs.get(0) == null || dbs.get(1) == null) {
            System.out.println("【29】【错误】用户收入数据汇总小于代码位维度收入数据汇总");
            throw new RuntimeException("【29】【错误】用户收入数据汇总小于代码位维度收入数据汇总");
        }

        List<BeanProxy> divideData = dbs.get(0);
        List<BeanProxy> tuData = dbs.get(1);

        Map<String, Double> divideDataMap = new HashMap<String, Double>();
        for (BeanProxy bp : divideData) {
            String key = bp.getInt("customerid") + "_" + bp.getInt("cid") + "_" + bp.getInt("tcm");
            divideDataMap.put(key, bp.getDouble("divide"));
        }

        for (BeanProxy bp : tuData) {
            String key = bp.getInt("customerid") + "_" + bp.getInt("cid") + "_" + bp.getInt("tcm");
            Double dd = divideDataMap.get(key);
            double td = bp.getDouble("divide");
            if ((dd == null && td >= valve) || (dd != null && td - dd.doubleValue() >= valve)) {
                BeanProxy error = new BeanProxy();
                // 用户id,计费名id,媒体id,分成收入汇总,代码位收入汇总,差额,差额比例
                error.setValue("customerid", bp.getInt("customerid"));
                error.setValue("cid", bp.getInt("cid"));
                error.setValue("tcm", bp.getInt("tcm"));
                error.setValue("divide1", dd);
                error.setValue("divide2", td);
                error.setValue("diff", dd - td);
                error.setValue("diffRate", (dd == null || dd.doubleValue() == 0) ? "-" : "" + (td - dd.doubleValue())
                        / dd.doubleValue() * 100 + "%");

                errorList.add(error);
            }
        }

        Collections.sort(errorList, new Comparator<BeanProxy>() {
            @Override
            public int compare(BeanProxy o1, BeanProxy o2) {
                double d1 = o1.getDouble("diff");
                double d2 = o2.getDouble("diff");
                return d1 == d2 ? 0 : d1 > d2 ? 1 : -1;
            }

        });
        return errorList;
    }

}
