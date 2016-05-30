package com.baidu.binlog.dump;

import org.slf4j.LoggerFactory;

import org.slf4j.Logger;
import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class CursorFile {
    Logger log = LoggerFactory.getLogger(CursorFile.class);

    int maxLines;
    int curFileNo = 0;
    int curFileLines = 0;
    String dirPath;
    FileWriter fw;

    public CursorFile(String dirPath, int maxLines) {
        this.dirPath = dirPath;
        this.maxLines = maxLines;
    }

    public String getLastLine() throws IOException {
        String[] ss = new File(dirPath).list();
        String lastLine = null;
        if (ss != null) {
            for (String s : ss) {
                int k = Integer.valueOf(s);
                if (curFileNo < k) {
                    curFileNo = k;
                }
            }

            File f = new File(dirPath + "/" + curFileNo);
            if (!f.exists()) {
                f.createNewFile();
            }

            BufferedReader br = new BufferedReader(new FileReader(f));
            String t;
            while ((t = br.readLine()) != null) {
                lastLine = t;
                curFileLines++;
            }
            br.close();
            fw = new FileWriter(f, true);
        } else {
            FileUtils.forceMkdir(new File(dirPath));
            File f = new File(dirPath + "/" + curFileNo);
            f.createNewFile();
            fw = new FileWriter(f, true);
        }

        log.info("last file: {},last line number: {},last line: {}", curFileNo, curFileLines, lastLine);
        return lastLine;
    }

    public void appendLine(String s) throws IOException {
        if (curFileLines++ >= maxLines) {
            fw.close();
            curFileNo++;
            File f = new File(dirPath + "/" + curFileNo);
            if (f.createNewFile()) {
                fw = new FileWriter(f, true);
                curFileLines = 0;
            } else {
                log.error("create file fail");
                throw new IOException("create file fail");
            }
        }

        fw.append(s).append("\n");
        fw.flush();
    }
}
