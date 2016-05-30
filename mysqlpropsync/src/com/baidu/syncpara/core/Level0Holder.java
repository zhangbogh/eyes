package com.baidu.syncpara.core;

import com.baidu.syncpara.cfg.SyncParaRow;

import org.stringtemplate.v4.ST;

import gnu.trove.map.TIntLongMap;
import gnu.trove.map.hash.TIntLongHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.TIntIntMap;

public class Level0Holder implements IHolder {
    TIntIntMap intMap = new TIntIntHashMap(100);
    TIntLongMap longMap = new TIntLongHashMap(100);
    TIntObjectMap<String> strMap = new TIntObjectHashMap<String>(100);

    @Override
    public void addRow(SyncParaRow row) {
        switch (row.getDatatype()) {
        case SyncPara.DATATYPE_INT:
            intMap.put(row.getId(), Integer.valueOf(row.getMvalue()));
            break;
        case SyncPara.DATATYPE_STRING:
            strMap.put(row.getId(), row.getMvalue());
            break;
        case SyncPara.DATATYPE_TEMPLATE:
            strMap.put(row.getId(), row.getMvalue());
            break;
        default:
            break;
        }
    }

    @Override
    public int getIntValue(int kid) {
        return intMap.get(kid);
    }

    @Override
    public String getStringValue(int kid) {
        return strMap.get(kid);
    }

    @Override
    public long getLongValue(int kid) {
        return longMap.get(kid);
    }

    @Override
    public String getTemplateValue(int kid, Object... objs) {
        ST st = new ST(strMap.get(kid));
        int i = 1;
        for (Object o : objs) {
            st.add("#" + i, o);
            i++;
        }
        return st.render();
    }
}
