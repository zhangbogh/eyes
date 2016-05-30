package com.union.check.engine;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLStreamException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.google.common.base.Charsets;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.io.Files;
import com.union.check.checker.AbstractChecker;
import com.union.check.checker.BasicdbChecker;
import com.union.check.checker.ComplexDbChecker;
import com.union.check.checker.FileChecker;
import com.union.check.checker.SequencedbChecker;
import com.union.check.checker.SnapdbChecker;
import com.union.check.checker.utils.FtpUtil;
import com.union.check.gen.Basenode;
import com.union.check.gen.BasicdbCheck;
import com.union.check.gen.ComplexDbCheck;
import com.union.check.gen.DbPort;
import com.union.check.gen.FileCheck;
import com.union.check.gen.FtpConn;
import com.union.check.gen.Mail;
import com.union.check.gen.Nodes;
import com.union.check.gen.SequencedbCheck;
import com.union.check.gen.SnapdbCheck;
import com.union.check.obj.DataBase;

import jodd.io.FileUtil;
import jodd.mail.Email;
import jodd.mail.SendMailSession;
import jodd.mail.SmtpServer;

public class CheckEngine {
    private static Logger log = LoggerFactory.getLogger(CheckEngine.class);

    private static HashMap<String, Object> id2Obj = new HashMap<String, Object>();
    private static HashMap<String, String> paras = new HashMap<String, String>();
    private static Multimap<String, String> mails = ArrayListMultimap.create();

    private static List<String> okNames = new LinkedList<String>();

    private Unmarshaller unmarshaller;
    private Marshaller marshaller;
    private String txt;

    public CheckEngine(String xmltxt) throws JAXBException, XMLStreamException {
        JAXBContext context = JAXBContext.newInstance(Nodes.class.getPackage().getName());
        unmarshaller = context.createUnmarshaller();
        marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
        txt = xmltxt;
    }

    public CheckEngine(File xml) throws Exception {
        this(Files.toString(xml, Charsets.UTF_8));
    }

    public static void addOks(String name) {
        okNames.add(name);
    }

    public static void putParas(String key, String value) {
        paras.put(key, value);
    }

    public static void addMail(String mailid, String html) {
        mails.put(mailid, html);
    }

    public static void putIdObj(String id, Object obj) {
        id2Obj.put(id, obj);
    }

    public static Object getObjById(String id) {
        return id2Obj.get(id);
    }

    public static void main(String[] args) throws JAXBException, IOException, ParseException, XMLStreamException {
        String name = null;
        if (args == null) {
            System.exit(1);
        } else if (args.length == 1) {
            name = args[0];
        }

        String filename = "./input/" + name + ".xml";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        MDC.put("filename", args[0] + "_" + sdf.format(new Date()));

        String txt = Files.toString(new File(filename), Charsets.UTF_8);
        CheckEngine engine = new CheckEngine(txt);

        engine.runXml();

        printMemUse();

        // 发送邮件
        sendMail();

        System.exit(0);
    }

    private static void sendMail() {
        SmtpServer smtpServer = new SmtpServer("mail2-in.baidu.com");
        SendMailSession session = smtpServer.createSession();
        session.open();
        Set<String> keys = mails.keySet();
        try {
            String mailt = FileUtil.readString(new File("./input/mail.tmpl"));

            for (String k : keys) {
                Mail m = (Mail) getObjById(k);
                Collection<String> s = mails.get(k);
                StringBuffer sb = new StringBuffer();
                for (String s1 : s) {
                    sb.append(s1).append("\n");
                }
                String p = mailt.replaceAll("#s", sb.toString());

                System.out.println(p);
                Email email = Email.create().from("union-rd@baidu.com").subject("【开目】业务校验出错，请关注").addHtml(p);
                String addres = m.getAddress();
                String[] ads = addres.split(",");
                email.setTo(ads);
                session.sendMail(email);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        session.close();
    }

    public void runXml() throws JAXBException, ParseException {
        // 全局变量的替换
        String txt = preProcess(this.txt);

        Nodes o1 = (Nodes) unmarshaller.unmarshal(new StringReader(txt));
        AbstractChecker checker = null;
        for (Object o : o1.getBasicdbCheckOrSnapdbCheckOrDbPort()) {
            StringWriter sw = new StringWriter();
            marshaller.marshal(o, sw);

            if (o instanceof BasicdbCheck) {
                checker = new BasicdbChecker((BasicdbCheck) o);
            } else if (o instanceof DbPort) {
                DbPort p = (DbPort) o;
                DataBase proxy = new DataBase(p);
                CheckEngine.putIdObj(p.getId(), proxy);
                checker = null;
            } else if (o instanceof Mail) {
                Mail p = (Mail) o;
                CheckEngine.putIdObj(p.getId(), p);
                checker = null;
            } else if (o instanceof FtpConn) {
                FtpConn p = (FtpConn) o;
                FtpUtil proxy = new FtpUtil(p);
                CheckEngine.putIdObj(p.getId(), proxy);
                checker = null;
            } else if (o instanceof SnapdbCheck) {
                checker = new SnapdbChecker((SnapdbCheck) o);
            } else if (o instanceof FileCheck) {
                checker = new FileChecker((FileCheck) o);
            } else if (o instanceof SequencedbCheck) {
                checker = new SequencedbChecker((SequencedbCheck) o);
            } else if (o instanceof ComplexDbCheck) {
                checker = new ComplexDbChecker((ComplexDbCheck) o);
            }

            if (checker != null) {
                log.info("begin:" + checker.getName());
                checker.check();
                log.info("over:" + checker.getName());
            }
        }
    }

    public void runXml(String id, boolean sendMail) throws Exception {
        // 全局变量的替换
        String txt = preProcess(this.txt);

        Nodes o1 = (Nodes) unmarshaller.unmarshal(new StringReader(txt));
        AbstractChecker checker = null;
        for (Object o : o1.getBasicdbCheckOrSnapdbCheckOrDbPort()) {
            StringWriter sw = new StringWriter();
            marshaller.marshal(o, sw);
            if (o instanceof Basenode) {
                if (((Basenode) o).getId().equals(id)) {
                    if (o instanceof SnapdbCheck) {
                        checker = new SnapdbChecker((SnapdbCheck) o);
                    } else if (o instanceof FileCheck) {
                        checker = new FileChecker((FileCheck) o);
                    } else if (o instanceof SequencedbCheck) {
                        checker = new SequencedbChecker((SequencedbCheck) o);
                    } else if (o instanceof ComplexDbCheck) {
                        checker = new ComplexDbChecker((ComplexDbCheck) o);
                    } else if (o instanceof BasicdbCheck) {
                        checker = new BasicdbChecker((BasicdbCheck) o);
                    }
                    if (checker != null) {
                        log.info("begin:" + checker.getName());
                        checker.check();
                        log.info("over:" + checker.getName());
                    }
                }

            } else if (o instanceof DbPort) {
                DbPort p = (DbPort) o;
                DataBase proxy = new DataBase(p);
                CheckEngine.putIdObj(p.getId(), proxy);
            } else if (o instanceof Mail) {
                Mail p = (Mail) o;
                CheckEngine.putIdObj(p.getId(), p);
            } else if (o instanceof FtpConn) {
                FtpConn p = (FtpConn) o;
                FtpUtil proxy = new FtpUtil(p);
                CheckEngine.putIdObj(p.getId(), proxy);
            }

        }
        if (sendMail) {
            sendMail();
        }
    }

    private static final void printMemUse() {
        Runtime run = Runtime.getRuntime();
        long max = run.maxMemory();
        long total = run.totalMemory();
        long free = run.freeMemory();
        double userate = ((double) total) / max;
        log.info("Memory info:");
        log.info("max = " + max);
        log.info("total = " + total);
        log.info("free = " + free);
        log.info("userate = " + userate);
    }

    // 替换全局变量
    private static String preProcess(String txt) throws ParseException {
        // 全局常量
        // yesterday
        Calendar c = Calendar.getInstance();
        putParas("month", new SimpleDateFormat("yyyyMM").format(c.getTime()));
        c.add(Calendar.DATE, -1);
        putParas("yesterday", new SimpleDateFormat("yyyyMMdd").format(c.getTime()));
        c.add(Calendar.DATE, -1);
        putParas("twodaysago", new SimpleDateFormat("yyyyMMdd").format(c.getTime()));
        putParas("twodaysago-", new SimpleDateFormat("yyyy-MM-dd").format(c.getTime()));
        c.add(Calendar.DATE, -2);
        putParas("fourdaysago", new SimpleDateFormat("yyyyMMdd").format(c.getTime()));
        c = Calendar.getInstance();
        c.add(Calendar.DATE, -10);
        putParas("tendaysago-", new SimpleDateFormat("yyyy-MM-dd").format(c.getTime()));
        c = Calendar.getInstance();
        c.add(Calendar.DATE, -30);
        putParas("thirtydaysago-", new SimpleDateFormat("yyyy-MM-dd").format(c.getTime()));

        c = Calendar.getInstance();
        c.add(Calendar.MONTH, -1);
        putParas("lastmonth", new SimpleDateFormat("yyyyMM").format(c.getTime()));
        putParas("lastmonth-", new SimpleDateFormat("yyyy-MM").format(c.getTime()));

        // 数据库变量长度重大到小排序
        Set<String> keys = paras.keySet();
        String[] kys = new String[keys.size()];
        kys = keys.toArray(kys);
        Arrays.sort(kys, new Comparator<String>() {
            public int compare(String o1, String o2) {
                return o2.length() - o1.length();
            }
        });
        // 字符长度重大到小排序

        for (String tk : kys) {
            txt = txt.replaceAll("#" + tk, paras.get(tk));
        }
        return txt;
    }
}
