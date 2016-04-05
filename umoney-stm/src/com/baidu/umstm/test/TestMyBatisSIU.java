package com.baidu.umstm.test;

import com.baidu.umstm.dao.DaoFactory;
import com.baidu.umstm.dao.IBizFlowDao;
import com.baidu.umstm.model.BizFlow;

import org.apache.ibatis.session.SqlSession;
import junit.framework.TestCase;

public class TestMyBatisSIU extends TestCase {

    public void testInsert() {
        BizFlow bf = new BizFlow();
        bf.setBid(1);
        bf.setCurstatus(2);
        SqlSession ss = DaoFactory.getSession();
        try {
            IBizFlowDao dao = ss.getMapper(IBizFlowDao.class);
            dao.insertBizFlow(bf);
            ss.commit();
        } finally {
            ss.close();
        }
    }
}
