package com.union.check.checker.utils;

import com.union.check.gen.FtpConn;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Ftp Client to check whether file is exist
 *
 * @author v_chenqianming
 * @time 2015/10/23
 */
public final class FtpUtil {
    private static final String DEFAULT_ENCODING = "GBK";
    private static final String TEMPORAL_FILE_NAME = "file";
    private FtpConn ftpConn;

    public FtpUtil(FtpConn ftpConn) {
        this.ftpConn = ftpConn;
    }

    /**
     * check whether file is exist
     *
     * @param dir      target directory to check
     * @param fileName target file name to check
     * @param minsize  the minimum file size to check, must large than or equal to it
     * @return is file exist
     */
    public boolean isFileExist(String dir, String fileName, int minsize) {
        boolean success = false;
        FTPClient ftp = new FTPClient();
        try {
            int reply;
            ftp.setControlEncoding(DEFAULT_ENCODING);
            ftp.connect(ftpConn.getHost(), ftpConn.getPort());
            ftp.login(ftpConn.getName(), ftpConn.getPassword());
            reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                return success;
            }
            ftp.changeWorkingDirectory(dir);
            // do not list all files, just list file name related information,
            // or it will transfer back large file info
            FTPFile[] fs = ftp.listFiles(fileName);
            for (FTPFile ff : fs) {
                if (ff.getName().equals(fileName)) {
                    success = ff.getSize() >= minsize;
                    // if want to download files by ftp , may use the following code
                    // OutputStream is = new FileOutputStream(localFile);
                    // ftp.retrieveFile(ff.getName(), is);
                    // is.close();
                    break;
                }
            }
            ftp.logout();
            // if want to debug the approach (wget), use following statement
            // throw new IOException();
        } catch (IOException e) {
            e.printStackTrace();
            // try another approach to get file
            try {
                // replace symbol '+'(space after encoding ) with symbol "%20"
                fileName = URLEncoder.encode(fileName, DEFAULT_ENCODING).replaceAll("\\+", "%20");
            } catch (UnsupportedEncodingException e1) {
                return false;
            }
            File file = new File(TEMPORAL_FILE_NAME);
            WGet.download(String.format("ftp://%s/%s/%s", ftpConn.getHost(), dir, fileName), file);
            if (success = file.exists()) {
                success = file.getTotalSpace() >= minsize;
                file.delete();
            } else {
                success = false;
            }
        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException ioe) {
                    // pass if exception occur
                }
            }
        }
        return success;
    }
}
