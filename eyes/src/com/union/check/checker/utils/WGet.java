package com.union.check.checker.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.concurrent.TimeUnit;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * 根据URL下载文件<br/>
 * Linux系统JDK如果下载失败,将会尝试使用WGet进行下载
 *
 * @author skyfalling
 */
public class WGet {
    private static final String DEFAULT_ENCODING = "GBK";
    private static final String TEMP_FILE_EXTENSION = ".tmp";
    private static final Logger logger = LoggerFactory.getLogger(WGet.class);

    /**
     * 下载文件
     *
     * @param urlString
     * @param savedPath
     * @return
     */
    public static boolean download(String urlString, String savedPath) {
        return download(urlString, new File(savedPath));
    }

    /**
     * 下载文件
     *
     * @param urlString
     * @param savedFile
     * @return
     */
    public static boolean download(String urlString, File savedFile) {
        File tmpFile = new File(savedFile.getPath() + TEMP_FILE_EXTENSION);
        boolean success = downloadByJdk(urlString, tmpFile);
        if (!success && !isWindows()) {
            success = downloadByOs(urlString, tmpFile);
            success |= tmpFile.exists();
        }
        if (success) {
            tmpFile.renameTo(savedFile);
        }
        return success;
    }


    /**
     * java下载
     *
     * @param urlString
     * @param savedFile
     * @return
     */
    public static boolean downloadByJdk(String urlString, File savedFile) {
        try {
            // url encoding is invalid in this method
            urlString = URLDecoder.decode(urlString, DEFAULT_ENCODING);
            URL url = new URL(urlString);
            InputStream inputStream = url.openStream();
            OutputStream outputStream = new FileOutputStream(savedFile);
            dumpStream(inputStream, outputStream);
            return true;
        } catch (Exception e) {
            logger.warn("==>file download fail: " + urlString);
        }
        return false;
    }

    public static boolean dumpStream(InputStream inStream, OutputStream outStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len = -1;
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        outStream.close();
        inStream.close();
        return true;
    }

    /**
     * 系统下载
     *
     * @param url
     * @param file
     * @return
     */
    public static boolean downloadByOs(String url, File file) {
        String wgetCmd = "wget -t10 \"" + url + "\" -O \"" + file.getAbsolutePath() + "\"";
        try {
            return CmdExecutor.exec(wgetCmd, TimeUnit.MINUTES.toSeconds(1000L)) == 0;
        } catch (Exception e) {
            // if debug
            // e.printStackTrace();
            // method e.getMessage() may get null
            logger.debug(e.getMessage());
        }
        logger.warn("file download fail: \n" + wgetCmd);
        return false;
    }

    /**
     * 判断是否为Windows系统
     *
     * @return
     */
    private static boolean isWindows() {
        String os = System.getProperties().getProperty("os.name", "");
        return os.toLowerCase().contains("windows");
    }

}
