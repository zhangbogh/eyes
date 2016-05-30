package com.baidu.binlog.dump;

import com.github.shyiko.mysql.binlog.BinaryLogClient;

import org.apache.commons.io.FileUtils;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class FileDumper {
    Logger log = LoggerFactory.getLogger(FileDumper.class);
    int time;
    int maxrecord;
    int currecord;
    FileWriter fw;
    File curFile;
    String path;
    DateFormat df;
    ArrayList<String> al;
    CursorFile cf;
    BinaryLogClient blc;
    String prefix;

    public FileDumper(String path, int time, int maxrecord, String prefix, CursorFile cf, BinaryLogClient blc)
            throws IOException, NoSuchAlgorithmException {
        this.time = time;
        this.prefix = prefix;
        this.path = path;
        this.maxrecord = maxrecord;
        this.cf = cf;
        this.blc = blc;

        df = new SimpleDateFormat("YYYYMMDDHHmmSS");
        FileUtils.forceMkdir(new File(path));
        al = new ArrayList<String>(maxrecord + 10);
    }

    public String createFileName() {
        return prefix + df.format(new Date());
    }

    public void start() {
        Timer timer = new Timer();
        FileDumper self = this;
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    log.info("try to flush to disk");
                    self.splitFile();
                } catch (IOException e) {
                    log.error("split file fail");
                    e.printStackTrace();
                }
            }
        }, 0, 1000 * time);
    }

    /**
     * 获取单个文件的MD5值！
     * 
     * @param file
     * @return
     */
    public String getFileMD5() {
        MessageDigest digest = null;
        FileInputStream in = null;
        byte buffer[] = new byte[2048];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(curFile);
            while ((len = in.read(buffer, 0, 2048)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        return bigInt.toString(16);
    }

    public synchronized void appendStr(String str) throws IOException {
        al.add(str);

        currecord++;
        if (currecord > maxrecord) {
            splitFile();
            currecord = 0;
        }
    }

    public final synchronized void splitFile() throws IOException {
        if (al.size() == 0) {
            return;
        }

        // 刷新内存数据
        curFile = new File(path + "/" + createFileName());
        curFile.createNewFile();
        fw = new FileWriter(curFile);
        for (String a : al) {
            fw.append(a).append("\n");
        }
        fw.flush();
        fw.close();
        al.clear();

        // 产生md5文件
        File md5File = new File(curFile.getAbsolutePath() + ".md5");
        md5File.createNewFile();
        FileWriter fw1 = new FileWriter(md5File);
        fw1.write(getFileMD5());
        fw1.flush();
        fw1.close();
        // 更新binlog游标
        cf.appendLine(blc.getBinlogFilename() + "#" + blc.getBinlogPosition());
    }
}
