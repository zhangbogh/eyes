package com.union.check.checker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.google.common.base.Strings;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.union.check.engine.CheckEngine;
import com.union.check.gen.SequencedbCheck;
import com.union.check.obj.BeanProxy;
import com.union.check.obj.DataBase;

public class SequencedbChecker extends AbstractChecker {
    SequencedbCheck node;
    double level1 = 0.3;
    double level2 = 0.15;

    public SequencedbChecker(SequencedbCheck node) {
        super(node);
        this.node = node;
    }

    @Override
    public void check() {
        DataBase db = (DataBase) CheckEngine.getObjById(node.getBdid());
        List<BeanProxy> dbs = db.query(node.getSql());
        switch (node.getSequencetype()) {
            case 1:
                Multimap<String, BeanProxy> map = makeMutilMap(dbs);
                datas = twoDayCompare(map);
                break;

            default:
                datas=dbs;
                break;
        }
        outputResult();
    }

    private List<BeanProxy> twoDayCompare(Multimap<String, BeanProxy> map) {
        List<BeanProxy> result = new ArrayList<BeanProxy>();
        Set<String> keys = map.keySet();
        String vkey = node.getSequencevalue();
        String fkey = node.getFollowvalue();
        double maxvalue = 0;
        for (String k : keys) {
            Collection<BeanProxy> c = map.get(k);
            if (c.size() > 1) {
                List<BeanProxy> lp = new ArrayList<BeanProxy>(c);
                double d0 = lp.get(0).getDouble(vkey);
                double d1 = lp.get(1).getDouble(vkey);
                double f0 = lp.get(0).getDouble(fkey);
                double f1 = lp.get(1).getDouble(fkey);
                if (d0 == 0 || d1 == 0) {
                    continue;
                }

                if (d0 > d1) {
                    if (d0 > 1000 && (d0 - d1) / d0 > level1) {
                        // 如果跟随变量变化率超过一半
                        if (f0 != 0 && f0 > f1 && (f0 - f1) / f0 > level2) {
                            continue;
                        }

                        // 相对大的值放在报告最上面的位置
                        if (d0 > maxvalue) {
                            maxvalue = d0;
                            result.add(0, lp.get(1));
                            result.add(0, lp.get(0));
                        } else {
                            result.add(lp.get(0));
                            result.add(lp.get(1));
                        }
                    }
                } else {
                    if (d1 > 1000 && (d1 - d0) / d1 > level1) {
                        // 如果跟随变量变化率超过一半
                        if (f1 != 0 && f1 > f0 && (f1 - f0) / f1 > level2) {
                            continue;
                        }

                        if (d1 > maxvalue) {
                            maxvalue = d1;
                            result.add(0, lp.get(1));
                            result.add(0, lp.get(0));
                        } else {
                            result.add(lp.get(0));
                            result.add(lp.get(1));
                        }
                    }
                }
            }
        }
        return result;
    }

    private Multimap<String, BeanProxy> makeMutilMap(List<BeanProxy> dbs) {
        String s = node.getSequencekey();
        Multimap<String, BeanProxy> result = ArrayListMultimap.create();
        String[] keys = s.split(",");

        for (BeanProxy bp : dbs) {
            String key = "";
            int i = 0;
            for (String k : keys) {
                key += (bp.getString(k) + "^");
            }
            if (!Strings.isNullOrEmpty(key)) {
                result.put(key, bp);
            }
        }
        return result;
    }


}
