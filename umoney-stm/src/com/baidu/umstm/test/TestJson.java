package com.baidu.umstm.test;

import org.json.JSONException;
import org.everit.json.schema.loader.SchemaLoader;
import org.everit.json.schema.Schema;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileNotFoundException;
import java.io.FileReader;

import junit.framework.TestCase;

public class TestJson extends TestCase {
    public void testJsonDataBind() {

    }

    public void testJsonValidator() throws JSONException, FileNotFoundException {
        JSONObject rawSchema = new JSONObject(new JSONTokener(new FileReader("./jsonschema/user-schema.json")));
        Schema schema = SchemaLoader.load(rawSchema);
        schema.validate(new JSONObject(new JSONTokener(new FileReader("./jsonobj/User1.json"))));
    }
}
