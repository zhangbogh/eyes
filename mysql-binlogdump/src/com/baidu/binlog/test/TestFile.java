package com.baidu.binlog.test;

import com.baidu.binlog.dump.CursorFile;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

public class TestFile extends TestCase {
    public void testList() {
        String[] fs = new File(".").list();
        for (String s : fs) {
            System.out.println(s);
        }
    }

    public void testCursorFile() throws IOException {
        CursorFile cf = new CursorFile("./cursor-dump", 10);
        cf.getLastLine();
        for (int i = 0; i < 10; i++) {
            cf.appendLine("test");
        }
    }
}
