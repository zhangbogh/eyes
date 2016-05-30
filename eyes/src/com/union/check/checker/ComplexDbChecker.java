package com.union.check.checker;

import java.util.ArrayList;
import java.util.List;

import com.union.check.engine.CheckEngine;
import com.union.check.exception.FileIoException;
import com.union.check.gen.ComplexDbCheck;
import com.union.check.obj.BeanProxy;
import com.union.check.obj.DataBase;

/**
 * 多数据库数据校验
 * 
 * @author yaoli
 * 
 */
public class ComplexDbChecker extends AbstractChecker {
    ComplexDbCheck node;

    public ComplexDbChecker(ComplexDbCheck node) {
        super(node);
        this.node = node;
    }

    @Override
    public void check() {
        DataBase db = (DataBase) CheckEngine.getObjById(node.getBdid());
        List<List<BeanProxy>> ds = new ArrayList<List<BeanProxy>>();

        for (String sql : node.getSql()) {
            List<BeanProxy> data = db.query(sql);
            ds.add(data);
        }

        try {
            Class<?> clz = Class.forName(node.getCheckconditionclass());
            ComplexCheckCondition scc = (ComplexCheckCondition) clz.newInstance();

            datas = scc.compare(ds);
        } catch (Exception e) {
            e.printStackTrace();
            throw new FileIoException();
        }

        outputResult();
    }
}
