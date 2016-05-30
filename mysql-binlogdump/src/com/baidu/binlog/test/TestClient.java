package com.baidu.binlog.test;

import com.github.shyiko.mysql.binlog.event.TableMapEventData;

import com.github.shyiko.mysql.binlog.event.WriteRowsEventData;
import com.github.shyiko.mysql.binlog.event.EventData;
import junit.framework.TestCase;
import com.github.shyiko.mysql.binlog.event.EventType;
import com.github.shyiko.mysql.binlog.event.Event;

import java.io.IOException;

import com.github.shyiko.mysql.binlog.BinaryLogClient.EventListener;
import com.github.shyiko.mysql.binlog.BinaryLogClient;

public class TestClient extends TestCase {
    public void testConnect() throws IOException {
        // 修改表结构的时候要注意，不能调整字段的位置，并且通知连接程序更新
        BinaryLogClient client = new BinaryLogClient("localhost", 3306, "root", "811231");
        client.registerEventListener(new EventListener() {

            @Override
            public void onEvent(Event event) {
                EventType et = event.getHeader().getEventType();
                switch (et) {
                case EXT_DELETE_ROWS:
                    System.out.println("delete row");
                    break;
                case EXT_UPDATE_ROWS:
                    System.out.println("update row");
                    break;
                case EXT_WRITE_ROWS:
                    System.out.println("insert row");
                    WriteRowsEventData ed = event.getData();
                    System.out.println(ed);
                    break;
                case TABLE_MAP:
                    System.out.println("table map");
                    TableMapEventData ed1 = event.getData();
                    System.out.println(ed1);
                    break;
                case QUERY:
                    System.out.println("query");
                    EventData ed2 = event.getData();
                    System.out.println(ed2);
                    break;
                default:
                    System.out.println(et);
                    break;
                }
                System.out.println(et + " " + client.getBinlogFilename() + " " + client.getBinlogPosition());
            }

        });

        client.setBinlogFilename("mysql-bin.000001");
        client.setBinlogPosition(411);
        client.connect();
    }
}
