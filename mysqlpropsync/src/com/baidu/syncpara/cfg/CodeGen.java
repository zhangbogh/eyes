package com.baidu.syncpara.cfg;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import com.google.gson.Gson;

public class CodeGen {
    static Config conf;
    static DataSource dataSource;

    public static void main(String[] args) throws JsonSyntaxException, JsonIOException, PropertyVetoException,
            IOException {
        conf = new Gson().fromJson(new FileReader("./conf/config.json"), Config.class);
        prepareDataBase();
        writeJavaFile();
    }

    private static void writeJavaFile() throws IOException {
        int t = conf.getClassname().lastIndexOf('.');
        String packagename = conf.getClassname().substring(0, t);
        String classname = conf.getClassname().substring(t + 1);

        StringBuffer sb = new StringBuffer();
        sb.append("package ").append(packagename).append(";\n\n");
        sb.append("public class ").append(classname).append(" {\n");
        List<SyncParaRow> rows = getRowsFromDb();
        for (SyncParaRow r : rows) {
            sb.append("    public final static int ");
            String k = r.getMkey().toUpperCase().replaceAll("\\.", "_");
            sb.append(k);
            sb.append(" = ");
            sb.append(r.getId());
            sb.append(";\n");
        }
        sb.append("}\n");
        File f = new File("./" + classname + ".java");
        f.createNewFile();
        FileWriter fw = new FileWriter(f);
        fw.write(sb.toString());
        fw.close();
    }

    private static List<SyncParaRow> getRowsFromDb() {
        List<SyncParaRow> result = new ArrayList<SyncParaRow>();
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("select id,key1 from syncparas");
            while (rs.next()) {
                SyncParaRow r = new SyncParaRow();
                r.setId(rs.getInt(1));
                r.setMkey(rs.getString(2));
                result.add(r);
            }
        } catch (Exception ex) {

        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    private static void prepareDataBase() throws PropertyVetoException {
        ComboPooledDataSource ds = new ComboPooledDataSource();
        ds.setDriverClass("com.mysql.jdbc.Driver");
        ds.setJdbcUrl(conf.getDbsrc().getUrl());
        ds.setUser(conf.getDbsrc().getUser());
        ds.setPassword(conf.getDbsrc().getPassword());
        ds.setMaxPoolSize(5);
        ds.setMinPoolSize(2);
        ds.setTestConnectionOnCheckin(true);
        ds.setIdleConnectionTestPeriod(18000);
        dataSource = ds;
    }

}
