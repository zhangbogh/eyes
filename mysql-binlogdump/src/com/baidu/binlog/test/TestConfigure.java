package com.baidu.binlog.test;

import com.baidu.binlog.model.Configure;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import com.google.gson.Gson;
import junit.framework.TestCase;

public class TestConfigure extends TestCase {
    public void testRead() throws JsonSyntaxException, JsonIOException, FileNotFoundException {
        Gson gson = new Gson();
        Configure conf = gson.fromJson(new FileReader(new File("./conf/config.json")), Configure.class);
        assertEquals(conf.getSrc().getPassword(), "811231");
    }
}
