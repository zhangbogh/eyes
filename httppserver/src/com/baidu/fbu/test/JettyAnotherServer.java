package com.baidu.fbu.test;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;

public class JettyAnotherServer {
    public static void main(String[] args) throws Exception {
        Server server = new Server(8090);
        ServletHandler handler = new ServletHandler();
        server.setHandler(handler);
        handler.addServletWithMapping(HelloServlet1.class, "/*");
        server.start();
        server.join();
    }
}
