package com.baidu.umstm.test;

import static java.lang.System.out;

import com.baidu.umstm.model.BizFlowData;

import com.google.gson.reflect.TypeToken;
import com.google.gson.Gson;
import org.json.JSONException;
import org.everit.json.schema.loader.SchemaLoader;
import org.everit.json.schema.Schema;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;

import junit.framework.TestCase;

public class TestJson extends TestCase {
    public void testJsonDataBind() {
        User u = new User();
        u.setAge(10);
        u.setName("zhangbo");
        BizFlowData<User> bfd = new BizFlowData<User>(u);
        bfd.addStepStatus(0, 0, "hello world");
        Gson gson = new Gson();
        Type type = new TypeToken<BizFlowData<User>>() {
        }.getType();
        String s = gson.toJson(bfd, type);
        out.println(s);
        BizFlowData<User> bfd1 = gson.fromJson(s, type);
        out.println(bfd1.getCusData().getName());
    }

    public void testJsonValidator() throws JSONException, FileNotFoundException {
        JSONObject rawSchema = new JSONObject(new JSONTokener(new FileReader("./jsonschema/user-schema.json")));
        Schema schema = SchemaLoader.load(rawSchema);
        schema.validate(new JSONObject(new JSONTokener(new FileReader("./jsonobj/User1.json"))));
    }
}
