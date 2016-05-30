package com.baidu.fbu.jetty.proxy;

import java.util.HashMap;

public class HttpJson {
    String uri;
    HashMap<String, String> head = new HashMap<String, String>();
    HashMap<String, String> body = new HashMap<String, String>();

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public HashMap<String, String> getHead() {
        return head;
    }

    public void setHead(HashMap<String, String> head) {
        this.head = head;
    }

    public HashMap<String, String> getBody() {
        return body;
    }

    public void setBody(HashMap<String, String> body) {
        this.body = body;
    }

}
