package com.baidu.binlog.recv;

import com.baidu.binlog.dump.CursorFile;
import com.baidu.binlog.dump.FileDumpListener;
import com.baidu.binlog.model.Configure;
import com.baidu.binlog.model.TableRecv;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.Gson;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.sql.DataSource;

public class DumpFileReciver {
    static Logger log = LoggerFactory.getLogger(DumpFileReciver.class);
    static DataSource dataSource;
    static Configure conf;
    static String curRecvFile;
    static CursorFile cf;
    static int missMd5;

    public static void main(String[] args) throws JsonSyntaxException, JsonIOException, PropertyVetoException,
            IOException {
        conf = new Gson().fromJson(new FileReader(new File("./conf/config-recv.json")), Configure.class);
        prepareDataBase();
        prepareCursor();
        begin();
    }

    private static void prepareCursor() throws IOException {
        cf = new CursorFile("./cursor-recv", 1000);
        curRecvFile = cf.getLastLine();
        if (curRecvFile == null) {
            curRecvFile = "";
        }
    }

    private static void begin() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    log.info("try to read disk");
                    readFile();
                } catch (IOException e) {
                    log.error("read file fail");
                    e.printStackTrace();
                }
            }

        }, 0, 1000 * conf.getRecvctrl().getTime());

    }

    private final static void readFile() throws IOException {
        String[] files = new File(conf.getRecvctrl().getFiledir()).list();
        Arrays.sort(files);
        for (String s : files) {
            if (!s.endsWith(".md5") && s.compareTo(curRecvFile) > 0) {
                // 没检验通过直接退出，不允许跳过
                if (!checkMd5(s)) {
                    missMd5++;
                    if (missMd5 > 2) {
                        log.error("lost md5 file for {}", s);
                    }
                    break;
                }
                missMd5 = 0;

                List<String> ls = Files.readAllLines(Paths.get(conf.getRecvctrl().getFiledir() + "/" + s));
                doIt(ls);
                // 完成时更新游标
                cf.appendLine(s);
                curRecvFile = s;
            }
        }
    }

    private static boolean checkMd5(String s) throws IOException {
        String computeMd5 = getFileMD5(conf.getRecvctrl().getFiledir() + "/" + s);
        List<String> l = Files.readAllLines(Paths.get(conf.getRecvctrl().getFiledir() + "/" + s + ".md5"));
        if (l.size() == 1 && l.get(0).trim().equals(computeMd5)) {
            return true;
        }
        return false;
    }

    /**
     * 获取单个文件的MD5值！
     * 
     * @param file
     * @return
     */
    public static String getFileMD5(String s) {
        MessageDigest digest = null;
        FileInputStream in = null;
        byte buffer[] = new byte[2048];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(new File(s));
            while ((len = in.read(buffer, 0, 2048)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        return bigInt.toString(16);
    }

    /**
     * 一个文件中的所有操作，通过一个事务执行
     * 
     * @param ls
     */
    private static void doIt(List<String> ls) {
        // 批量生成sql
        List<String> sqls = new ArrayList<String>();
        for (String r : ls) {
            String[] ss = r.split("\t");
            int type = Integer.valueOf(ss[1]);
            switch (type) {
            case FileDumpListener.ADD:
                for (TableRecv tr : conf.getTablerecvs()) {
                    if (tr.getTableno() == Integer.valueOf(ss[0])) {
                        sqls.addAll(replaceSql(ss, tr.getInsertsql()));
                    }
                }
                break;
            case FileDumpListener.DEL:
                for (TableRecv tr : conf.getTablerecvs()) {
                    if (tr.getTableno() == Integer.valueOf(ss[0])) {
                        sqls.addAll(replaceSql(ss, tr.getDeletesql()));
                    }
                }
                break;
            case FileDumpListener.UPDATE:
                for (TableRecv tr : conf.getTablerecvs()) {
                    if (tr.getTableno() == Integer.valueOf(ss[0])) {
                        sqls.addAll(replaceSql(ss, tr.getUpdatesql()));
                    }
                }
                break;
            default:
                break;
            }
        }

        // 批量执行sql
        Connection con = null;
        try {
            con = dataSource.getConnection();
            con.setAutoCommit(false);
            Statement st = con.createStatement();
            for (String s : sqls) {
                st.execute(s);
            }
            con.commit();
            con.setAutoCommit(true);
        } catch (Exception ex) {
            log.error("execute fail");
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private static Collection<? extends String> replaceSql(String[] ss, List<String> insertsql) {
        List<String> result = new ArrayList<String>();
        for (String ist : insertsql) {
            int i = 0;
            String t = ist;
            for (String s : ss) {
                t = t.replaceAll("#" + i, s);
                i++;
            }
            result.add(t);
        }
        return result;
    }

    private static void prepareDataBase() throws PropertyVetoException {
        ComboPooledDataSource ds = new ComboPooledDataSource();
        ds.setDriverClass("com.mysql.jdbc.Driver");
        ds.setJdbcUrl(conf.getDest().getUrl());
        ds.setUser(conf.getDest().getUser());
        ds.setPassword(conf.getDest().getPassword());
        ds.setMaxPoolSize(5);
        ds.setMinPoolSize(2);
        ds.setTestConnectionOnCheckin(true);
        ds.setIdleConnectionTestPeriod(18000);
        dataSource = ds;
    }
}
