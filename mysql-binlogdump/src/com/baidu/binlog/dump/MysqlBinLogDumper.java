package com.baidu.binlog.dump;

import com.baidu.binlog.model.Configure;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import javax.sql.DataSource;

import com.google.gson.Gson;

public class MysqlBinLogDumper {
    static DataSource dataSource;
    static BinaryLogClient binLogClient;

    public static void main(String[] args) throws JsonSyntaxException, JsonIOException, IOException,
            NoSuchAlgorithmException, PropertyVetoException {
        Configure conf = new Gson().fromJson(new FileReader(new File("./conf/config.json")), Configure.class);
        prepareDataBase(conf);
        prepareClient(conf);
    }

    private static void prepareClient(Configure conf) throws IOException, NoSuchAlgorithmException {
        binLogClient = new BinaryLogClient(conf.getSrc().getIp(), 3306, conf.getSrc().getUser(), conf.getSrc()
                .getPassword());
        FileDumpListener fdl = new FileDumpListener(conf, binLogClient, dataSource);
        binLogClient.registerEventListener(fdl);

        // 线程悬停
        binLogClient.connect();
    }

    private static void prepareDataBase(Configure conf) throws PropertyVetoException {
        ComboPooledDataSource ds = new ComboPooledDataSource();
        ds.setDriverClass("com.mysql.jdbc.Driver");
        ds.setJdbcUrl(conf.getSrc().getUrl());
        ds.setUser(conf.getSrc().getUser());
        ds.setPassword(conf.getSrc().getPassword());
        ds.setMaxPoolSize(5);
        ds.setMinPoolSize(2);
        ds.setTestConnectionOnCheckin(true);
        ds.setIdleConnectionTestPeriod(18000);
        dataSource = ds;
    }

}
