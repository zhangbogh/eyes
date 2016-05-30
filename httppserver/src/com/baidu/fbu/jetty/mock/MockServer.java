package com.baidu.fbu.jetty.mock;

import com.baidu.fbu.jetty.proxy.HttpJson;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.CommandLine;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.server.Server;

public class MockServer {
    static Logger log = LoggerFactory.getLogger(MockServer.class);
    public static CommandLine cli;
    private static Options options;
    public static String LOCAL_PORT = "lp";
    public static String CONF = "cf";

    private static boolean recordFlag = false;
    private static List<HttpJson> memList = new ArrayList<HttpJson>();
    private static Gson gson = new Gson();

    public synchronized static void beginRecord() {
        recordFlag = true;
    }

    public static boolean getRecordFlag() {
        return recordFlag;
    }

    public synchronized static void stopRecord() {
        recordFlag = false;
    }

    public synchronized static void addHttp(HttpJson hj) {
        memList.add(hj);
    }

    public synchronized static void clearHttp() {
        memList.clear();
    }

    public static String getHttpJson() {
        return gson.toJson(memList);
    }

    static {
        options = new Options();
        Option lport = new Option("lp", "lport", true, "local http proxy port");
        Option conf = new Option("cf", "conf", true, "http request/response msg");
        options.addOption(lport).addOption(conf);
    }

    public static void main(String[] args) throws Exception {
        args = new String[] { "-lp", "8080", "-cf", "mock.json" };

        if (args.length == 1 && args[0].equals("help")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("proxy server", options);
        }

        cli = new DefaultParser().parse(options, args);

        Server server = new Server(Integer.valueOf(cli.getOptionValue(LOCAL_PORT)));
        ServletHandler handler = new ServletHandler();
        server.setHandler(handler);
        handler.addServletWithMapping(MockServlet.class, "/*");
        server.start();
        server.join();
    }
}
