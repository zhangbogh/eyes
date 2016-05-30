package com.union.check.checker;

import java.util.List;

import com.union.check.obj.BeanProxy;

public interface ComplexCheckCondition {
    /**
     * 多个数据源数据进行比对
     * 
     * @param dbs
     * @return
     */
    public List<BeanProxy> compare(List<List<BeanProxy>> dbs);
}
