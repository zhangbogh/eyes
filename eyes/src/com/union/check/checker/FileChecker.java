package com.union.check.checker;

import com.union.check.checker.utils.CronUtil;
import com.union.check.checker.utils.FtpUtil;
import com.union.check.checker.utils.Preprocessor;
import com.union.check.engine.CheckEngine;
import com.union.check.gen.FileCheck;
import com.union.check.obj.BeanProxy;

import java.util.ArrayList;

/**
 * Check whether file is exist
 *
 * @author v_chenqianming
 * @time 2015/10/23
 */
public class FileChecker extends AbstractChecker {
    private FileCheck node;

    public FileChecker(FileCheck node) {
        super(node);
        this.node = node;
        datas = new ArrayList<BeanProxy>();
    }

    @Override
    public void check() {
        if (!CronUtil.canBeScheduled(node.getCron())) {
            return;
        }
        FtpUtil ftpUtil = (FtpUtil) CheckEngine.getObjById(node.getFtpid());
        String dir = Preprocessor.process(node.getDir());
        int minsize = node.getMinsize();
        for (String fileItems : node.getFile()) {
            fileItems = Preprocessor.process(fileItems.trim());
            String[] files = fileItems.split(",|;");
            for (String item : files) {
                String fileName = item.trim();
                if (!ftpUtil.isFileExist(dir, fileName, minsize)) {
                    BeanProxy bp = new BeanProxy();
                    bp.setValue("Target Dir", dir);
                    bp.setValue("File Name", fileName);
                    datas.add(bp);
                }
            }
        }
        outputResult();
    }
}
