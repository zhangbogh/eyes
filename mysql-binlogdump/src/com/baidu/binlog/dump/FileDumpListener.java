package com.baidu.binlog.dump;

import com.baidu.binlog.model.Configure;
import com.baidu.binlog.model.TableDump;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import com.github.shyiko.mysql.binlog.event.UpdateRowsEventData;
import com.github.shyiko.mysql.binlog.event.DeleteRowsEventData;
import com.github.shyiko.mysql.binlog.event.EventType;
import com.github.shyiko.mysql.binlog.event.TableMapEventData;
import com.github.shyiko.mysql.binlog.event.WriteRowsEventData;

import java.io.IOException;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;

import javax.sql.DataSource;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.github.shyiko.mysql.binlog.event.Event;
import com.github.shyiko.mysql.binlog.BinaryLogClient.EventListener;

public class FileDumpListener implements EventListener {
    Logger log = LoggerFactory.getLogger(FileDumpListener.class);

    public static final int ADD = 0;
    public static final int DEL = 1;
    public static final int UPDATE = 2;

    FileDumper fd;
    HashSet<String> tableSet;
    TableCache tc;
    HashMap<String, TableDump> mapDump;
    DataSource dataSource;
    SimpleDateFormat sdf = new SimpleDateFormat("YYYYMMDD");

    public FileDumpListener(Configure conf, BinaryLogClient client, DataSource ds) throws NoSuchAlgorithmException,
            IOException {
        CursorFile cf = new CursorFile("./cursor", 1000);
        String s = cf.getLastLine();
        if (s != null) {
            String[] ss = s.split("#");
            client.setBinlogFilename(ss[0]);
            client.setBinlogPosition(Integer.valueOf(ss[1]));
        } else {
            client.setBinlogFilename(conf.getDumpctrl().getBinlogfile());
            client.setBinlogPosition(conf.getDumpctrl().getBinlogpos());
        }
        fd = new FileDumper("./data", conf.getDumpctrl().getTime(), conf.getDumpctrl().getMaxrecord(), conf
                .getDumpctrl().getFileprefix(), cf, client);

        tableSet = new HashSet<String>();
        mapDump = new HashMap<String, TableDump>();
        for (TableDump td : conf.getTabledumps()) {
            tableSet.add(td.getTablename());
            mapDump.put(td.getTablename(), td);
        }

        tc = new TableCache(3000);

        dataSource = ds;

        fd.start();
    }

    @Override
    public void onEvent(Event event) {
        EventType et = event.getHeader().getEventType();
        log.info("receive event:{}", et);
        try {
            switch (et) {
            case EXT_DELETE_ROWS:
                DeleteRowsEventData dred = event.getData();
                doIt(DEL, dred.getTableId(), getIds(dred.getTableId(), dred.getRows()));
                break;
            case EXT_UPDATE_ROWS:
                UpdateRowsEventData ured = event.getData();
                doIt(UPDATE, ured.getTableId(), getIdEntry(ured.getTableId(), ured.getRows()));
                break;
            case EXT_WRITE_ROWS:
                WriteRowsEventData wred = event.getData();
                doIt(ADD, wred.getTableId(), getIds(wred.getTableId(), wred.getRows()));
                break;
            case TABLE_MAP:
                TableMapEventData tmed = event.getData();
                if (tableSet.contains(tmed.getTable())) {
                    tc.putTable(tmed.getTableId(), tmed.getTable());
                }
                break;
            case QUERY:
                // 表结构更改
                break;
            default:
                break;
            }
        } catch (Exception ex) {
            log.error("record error");
        }
    }

    private List<String> getIdEntry(long tableId, List<Entry<Serializable[], Serializable[]>> rows) {
        String tablename = tc.getTable(tableId);
        List<String> ids = new ArrayList<String>(rows.size());
        if (tableSet.contains(tablename)) {
            TableDump td = mapDump.get(tablename);
            // key is before,value is after,but the id should not modify
            for (Entry<Serializable[], Serializable[]> entry : rows) {
                ids.add(entry.getValue()[td.getIdfieldno()].toString());
            }
        }
        return ids;
    }

    private List<String> getIds(long tableId, List<Serializable[]> rows) {
        String tablename = tc.getTable(tableId);
        List<String> ids = new ArrayList<String>(rows.size());
        if (tableSet.contains(tablename)) {
            TableDump td = mapDump.get(tablename);
            for (Serializable[] cols : rows) {
                ids.add(cols[td.getIdfieldno()].toString());
            }
        }
        return ids;
    }

    private void doIt(int type, long tid, List<String> ids) throws IOException {
        for (String id : ids) {
            switch (type) {
            case DEL:
                doDel(tid, id);
                break;
            case UPDATE:
                doUpdate(tid, id);
                break;
            case ADD:
                doAdd(tid, id);
                break;
            default:
                break;
            }
        }
    }

    private void doAdd(long tid, String id) throws IOException {
        TableDump td = mapDump.get(tc.getTable(tid));
        String sql = td.getDumpsql().replace("{}", id);
        StringBuilder sb = new StringBuilder();
        sb.append(td.getTableno()).append("\t").append(ADD);
        doOutput(sql, sb);
        fd.appendStr(sb.toString());
    }

    private void doOutput(String sql, StringBuilder sb) {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            ResultSetMetaData rsmd = rs.getMetaData();
            if (rs.next()) {
                for (int i = 0; i < rsmd.getColumnCount(); i++) {
                    int col = i + 1;
                    int dataType = rsmd.getColumnType(col);
                    sb.append("\t");
                    switch (dataType) {
                    case Types.VARCHAR:
                    case Types.CHAR:
                        sb.append(rs.getString(col));
                        break;
                    case Types.BIGINT:
                        sb.append(rs.getLong(col));
                        break;
                    case Types.INTEGER:
                    case Types.SMALLINT:
                        sb.append(rs.getInt(col));
                        break;
                    case Types.DOUBLE:
                        sb.append(rs.getDouble(col));
                        break;
                    case Types.DATE:
                        sb.append(sdf.format(rs.getDate(col)));
                        break;
                    default:
                        log.error("data type in sql not find");
                        break;
                    }
                }
            }
        } catch (Exception ex) {
            log.error("execute query fail");
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void doUpdate(long tid, String id) throws IOException {
        TableDump td = mapDump.get(tc.getTable(tid));
        // 执行sql
        String sql = td.getDumpsql().replace("{}", id);
        StringBuilder sb = new StringBuilder();
        sb.append(td.getTableno()).append("\t").append(UPDATE);
        doOutput(sql, sb);
        fd.appendStr(sb.toString());
    }

    private void doDel(long tid, String id) throws IOException {
        TableDump td = mapDump.get(tc.getTable(tid));
        fd.appendStr(td.getTableno() + "\t" + DEL + "\t" + id);
    }
}
