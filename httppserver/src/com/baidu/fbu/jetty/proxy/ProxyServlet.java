package com.baidu.fbu.jetty.proxy;

import org.eclipse.jetty.util.Fields;
import org.eclipse.jetty.client.util.FormContentProvider;
import org.eclipse.jetty.client.HttpRequest;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.HttpClient;

import java.io.IOException;
import java.util.Enumeration;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ProxyServlet extends HttpServlet {
    static Logger log = LoggerFactory.getLogger(ProxyServlet.class);
    private static final long serialVersionUID = 1L;
    private HttpClient client;

    public ProxyServlet() {
        client = new HttpClient();
        try {
            client.start();
        } catch (Exception e) {
            System.out.println("client start error");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doProxy(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String s = request.getRequestURI();
        log.info("receive request by get,uri is " + s);
        // 指令集部分
        if (s.startsWith(ProxyServer.cli.getOptionValue(ProxyServer.QUERY_PATH))) {
            String t = s.substring(s.lastIndexOf("/") + 1);
            switch (t) {
            case "get":
                log.info("get request info from memory");
                responseMemory(response);
                return;
            case "start":
                log.info("begin write request into memory");
                ProxyServer.beginRecord();
                return;
            case "end":
                log.info("end write request to memory,and clear it");
                ProxyServer.stopRecord();
                ProxyServer.clearHttp();
                return;
            default:
                log.error("error query path");
                return;
            }
        }
    }

    private void doProxy(HttpServletRequest request, HttpServletResponse response) throws IOException {
        dumpServletRequest(request);
        String s = request.getRequestURI();
        // 正常请求部分
        log.info("receive proxy request from " + s);
        String dr = ProxyServer.cli.getOptionValue(ProxyServer.DEST_ADDRESS) + s;
        log.info("post proxy request to " + dr);
        HttpRequest r = (HttpRequest) client.POST(dr);

        HttpJson hj = new HttpJson();
        if (s != null && s.length() > 1) {
            hj.setUri(s.substring(1));
        }
        StringBuilder sb = new StringBuilder();
        Enumeration<String> hnames = request.getHeaderNames();
        while (hnames.hasMoreElements()) {
            String n = hnames.nextElement();
            String nv = request.getHeader(n);
            sb.append(n).append(":").append(nv).append(", ");
            if (ProxyServer.getRecordFlag()) {
                hj.head.put(n, nv);
            }
            r.header(n, nv);
        }
        log.info("head is " + sb.toString());

        Enumeration<String> pnames = request.getParameterNames();
        sb = new StringBuilder();
        Fields fs = new Fields();
        while (pnames.hasMoreElements()) {
            String n = pnames.nextElement();
            String nv = request.getParameter(n);
            sb.append(n).append(":").append(nv).append(", ");
            if (ProxyServer.getRecordFlag()) {
                hj.body.put(n, nv);
            }
            fs.add(n, nv);
        }
        FormContentProvider fcp = new FormContentProvider(fs);
        r.content(fcp);
        log.info("para is " + sb.toString());

        if (ProxyServer.getRecordFlag()) {
            ProxyServer.addHttp(hj);
        }

        try {
            dumpHttpRequest(r);
            ContentResponse rsp = r.send();

            HttpFields hf = rsp.getHeaders();
            Enumeration<String> fnames = hf.getFieldNames();
            while (fnames.hasMoreElements()) {
                String n = fnames.nextElement();
                response.addHeader(n, hf.get(n));
            }
            response.getOutputStream().write(rsp.getContent());
            return;
        } catch (InterruptedException | TimeoutException | ExecutionException e) {
            log.error("post request error!");
        }

        log.info(dr + " time out");
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_GATEWAY_TIMEOUT);
    }

    private void dumpServletRequest(HttpServletRequest request) {
        // TODO Auto-generated method stub

    }

    private void dumpHttpRequest(HttpRequest r) {
        // TODO Auto-generated method stub

    }

    private void responseMemory(HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(ProxyServer.getHttpJson());
    }
}
