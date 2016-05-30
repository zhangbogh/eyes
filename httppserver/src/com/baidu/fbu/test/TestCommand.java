package com.baidu.fbu.test;

import org.apache.commons.cli.CommandLine;

import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import junit.framework.TestCase;

public class TestCommand extends TestCase {
    Options options = new Options();

    @Override
    protected void setUp() throws Exception {
        Option lport = new Option("lp", "lport", true, "local http proxy port");
        Option dest = new Option("de", "dest", true, "target system address with port");
        Option qpath = new Option("qp", "qpath", true, "query info path,include start/get/end three sub path");
        options.addOption(lport).addOption(dest).addOption(qpath);
    }

    public void testCommand() throws ParseException {
        // create the parser
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        String[] args = new String[] { "-lp", "8080" };
        CommandLine cl = parser.parse(options, args);
        System.out.println(cl.getOptionValue("lp"));
        formatter.printHelp("test", options);
    }
}
