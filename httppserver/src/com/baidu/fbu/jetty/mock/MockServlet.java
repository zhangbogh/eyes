package com.baidu.fbu.jetty.mock;

import com.jayway.jsonpath.JsonPath;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MockServlet extends HttpServlet {
    static Logger log = LoggerFactory.getLogger(MockServlet.class);
    private static final long serialVersionUID = 1L;
    HashMap<String, String> map = new HashMap<String, String>();

    Pattern p = Pattern.compile("#(.*?)#");

    public MockServlet() {
        String file = MockServer.cli.getOptionValue(MockServer.CONF);
        try {
            parseMap(file);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void parseMap(String file) throws IOException {
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        String s;
        boolean isReq = false;
        StringBuilder req = new StringBuilder();
        StringBuilder rsp = new StringBuilder();
        while ((s = br.readLine()) != null) {
            if (s.equals("req:")) {
                isReq = true;
                continue;
            } else if (s.equals("rsp:")) {
                isReq = false;
                continue;
            } else if (s.startsWith("----------")) {
                map.put(req.toString(), rsp.toString());
                req = new StringBuilder();
                rsp = new StringBuilder();
                continue;
            }

            if (isReq) {
                // req
                req.append(s);
            } else {
                // rsp
                rsp.append(s).append("\n");
            }

        }
        br.close();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String s = req.getRequestURI();
        log.info("receive request by get,uri is \n" + s);
        if (map.containsKey(s)) {
            String result = map.get(s);
            result = replaceResult(req, result);
            response(resp, result);
        }
    }

    private String replaceResult(HttpServletRequest req, String result) {
        String ss[] = result.split("\n");
        StringBuilder sb = new StringBuilder();
        for (String s : ss) {
            Matcher m = p.matcher(s);
            if (m.find()) {
                String t = m.group(1);
                int k = t.indexOf(".");
                String reqpars = t.substring(0, k);
                String path = t.substring(k + 1);
                String rt = req.getParameter(reqpars);
                String p = JsonPath.read(rt, path);
                sb.append(s.replaceAll("#.*?#", p));
            } else {
                sb.append(s);
            }
        }
        return sb.toString();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    private void response(HttpServletResponse response, String result) throws IOException {
        log.info("response result is " + result);
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(result);
    }
}
