package com.union.check;

import java.io.File;

import com.union.check.engine.CheckEngine;

/**
 * Created by scorpio on 16/1/13.
 */
public class CheckerTest {

    public static void main(String[] args) throws Exception {
        File file = new File("input/union_day_check.xml");
        CheckEngine checkEngine = new CheckEngine(file);
        checkEngine.runXml("31", true);
    }
}
