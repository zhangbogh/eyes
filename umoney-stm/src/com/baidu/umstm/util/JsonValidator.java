package com.baidu.umstm.util;

/**
 * 所有参数的校验逻辑通过配置json schema 完成 对外界所有的入參进行校验，校验不能做在对象层次，只能做在json层，因为默认值的问题
 * 
 * @author baidu
 *
 */
public class JsonValidator {

    public JsonValidator(String schema) {

    }

    public boolean check(String json) {
        return true;
    }

    public String getCheckResult() {
        return null;
    }
}
