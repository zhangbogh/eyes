package com.baidu.fbu.jetty.proxy;

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

public class ProxyServer {
    static Logger log = LoggerFactory.getLogger(ProxyServer.class);
    public static CommandLine cli;
    private static Options options;
    public static String LOCAL_PORT = "lp";
    public static String DEST_ADDRESS = "de";
    public static String QUERY_PATH = "qp";

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
        Option dest = new Option("de", "dest", true, "target system address with port");
        Option qpath = new Option("qp", "qpath", true, "query info path,include start/get/end three sub path");
        options.addOption(lport).addOption(dest).addOption(qpath);
    }

    public static void main(String[] args) throws Exception {
        if (args.length == 1 && args[0].equals("help")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("proxy server", options);
        }

        cli = new DefaultParser().parse(options, args);

        Server server = new Server(Integer.valueOf(cli.getOptionValue(LOCAL_PORT)));
        ServletHandler handler = new ServletHandler();
        server.setHandler(handler);
        handler.addServletWithMapping(ProxyServlet.class, "/*");
        server.start();
        server.join();
    }
}
