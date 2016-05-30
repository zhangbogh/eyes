package com.baidu.syncpara.core;

import com.baidu.syncpara.cfg.Config;
import com.baidu.syncpara.cfg.SyncParaRow;
import com.baidu.syncpara.exception.DataTypeNotMatchException;
import com.baidu.syncpara.exception.LevelNotFindException;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import java.beans.PropertyVetoException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import com.google.gson.Gson;

public class SyncPara {
    static SyncPara ins;

    static final int DATATYPE_STRING = 0;
    static final int DATATYPE_INT = 1;
    static final int DATATYPE_TEMPLATE = 5;

    static final int LEVEL_HEAP = 0;
    static final int LEVEL_OFFHEAP = 1;
    static final int LEVEL_REDIS = 2;
    static final int LEVEL_MYSQL = 3;

    static final String MAGIC_NAME = "syncpara_";

    int[][] dt_level_pos;

    String[] strValues;
    int[] intValues;
    String[] tmpValues;

    static DataSource dataSource;
    static JedisPool jpool;

    static {
        ins = new SyncPara();
    }

    private SyncPara() {
        try {
            Config conf = new Gson().fromJson(new FileReader("./conf/config.json"), Config.class);
            prepareDataBase(conf);
            prepareRedis(conf);
            List<SyncParaRow> l = getRowsFromDb();
            prepareDatas(l);
        } catch (JsonSyntaxException | JsonIOException | FileNotFoundException | PropertyVetoException e) {
            e.printStackTrace();
        }
    }

    private void prepareRedis(Config conf) {
        // 创建连接池
        jpool = new JedisPool(conf.getRedispool().getIp(), conf.getRedispool().getPort());
    }

    private void prepareDatas(List<SyncParaRow> l) {
        dt_level_pos = new int[l.size()][3];

        ArrayList<Integer> iList = new ArrayList<Integer>(l.size());
        ArrayList<String> strList = new ArrayList<String>(l.size());
        ArrayList<String> tmpList = new ArrayList<String>(l.size());

        int i = 0;
        for (SyncParaRow s : l) {
            dt_level_pos[i][1] = s.getLevel();
            if (s.getDatatype() == DATATYPE_INT) {
                dt_level_pos[i][0] = DATATYPE_INT;
                if (s.getLevel() == LEVEL_HEAP) {

                }
                dt_level_pos[i][2] = iList.size();
                iList.add(Integer.valueOf(s.getMvalue()));
            } else if (s.getDatatype() == DATATYPE_STRING && s.getLevel() == LEVEL_HEAP) {
                dt_level_pos[i][0] = DATATYPE_STRING;
                dt_level_pos[i][2] = strList.size();
                strList.add(s.getMvalue());
            } else if (s.getDatatype() == DATATYPE_TEMPLATE && s.getLevel() == LEVEL_HEAP) {
                dt_level_pos[i][0] = DATATYPE_TEMPLATE;
                dt_level_pos[i][2] = tmpList.size();
                tmpList.add(s.getMvalue());
            }
            i++;
        }

        intValues = new int[iList.size()];
        for (int k = 0; k < iList.size(); k++) {
            intValues[k] = iList.get(k);
        }

        strValues = new String[strList.size()];
        strValues = strList.toArray(strValues);

        tmpValues = new String[tmpList.size()];
        tmpValues = tmpList.toArray(tmpValues);
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
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    private static void prepareDataBase(Config conf) throws PropertyVetoException {
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

    public static SyncPara ins() {
        return ins;
    }

    public int getIntValue(int id) {
        if (dt_level_pos[id][0] == DATATYPE_INT) {
            if (dt_level_pos[id][1] == LEVEL_HEAP) {
                return intValues[dt_level_pos[id][2]];
            } else if (dt_level_pos[id][1] == LEVEL_REDIS) {
                Jedis j = jpool.getResource();
                int k = Integer.valueOf(j.get(MAGIC_NAME + id));
                j.close();
                return k;
            } else {
                throw new LevelNotFindException();
            }
        } else {
            throw new DataTypeNotMatchException();
        }
    }

    public String getStringValue(int id) throws DataTypeNotMatchException {
        if (dt_level_pos[id][0] == DATATYPE_STRING) {
            if (dt_level_pos[id][1] == LEVEL_HEAP) {
                return strValues[dt_level_pos[id][2]];
            } else if (dt_level_pos[id][1] == LEVEL_REDIS) {
                Jedis j = jpool.getResource();
                String k = j.get(MAGIC_NAME + id);
                j.close();
                return k;
            } else {
                throw new LevelNotFindException();
            }
        } else {
            throw new DataTypeNotMatchException();
        }
    }

    public String getTemplateValue(int id, Object... objs) throws DataTypeNotMatchException {
        if (dt_level_pos[id][0] == DATATYPE_TEMPLATE) {
            String tmp = "";
            if (dt_level_pos[id][1] == LEVEL_HEAP) {
                return tmpValues[dt_level_pos[id][2]];
            } else if (dt_level_pos[id][1] == LEVEL_REDIS) {
                Jedis j = jpool.getResource();
                tmp = j.get(MAGIC_NAME + id);
                j.close();
            } else {
                throw new LevelNotFindException();
            }

            int i = 0;
            for (Object o : objs) {
                tmp = tmp.replaceAll("#" + i, o.toString());
                i++;
            }
            return tmp;
        } else {
            throw new DataTypeNotMatchException();
        }
    }
}
