package com.baidu.umstm.dao;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.io.Resources;

import java.io.IOException;
import java.io.Reader;

import org.apache.ibatis.session.SqlSessionFactory;

public class DaoFactory {
    public static SqlSessionFactory ssf;
    static {
        Reader reader;
        try {
            reader = Resources.getResourceAsReader("conf.xml");
            ssf = new SqlSessionFactoryBuilder().build(reader);
            ssf.getConfiguration().addMapper(IBizFlowDao.class);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static SqlSession getSession() {
        return ssf.openSession();
    }
}
