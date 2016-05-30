package com.union.check.checker.utils;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;

import java.util.concurrent.TimeUnit;

/**
 * 执行命令行的工具类
 *
 * @author skyfalling
 */
public class CmdExecutor {

    public static final boolean isWindows;

    static {
        // 判断是否为Windows系统
        isWindows = System.getProperties().getProperty("os.name", "").toLowerCase().contains("windows");
    }

    /**
     * 执行脚本命令
     *
     * @param command
     * @return
     * @throws Exception
     */
    public static int exec(String command) throws Exception {
        return exec(command, 0);
    }

    /**
     * 执行脚本命令
     *
     * @param command
     * @param timeout 超时时间,单位秒
     * @return
     * @throws Exception
     */
    public static int exec(String command, long timeout) throws Exception {

        CommandLine cmdLine = buildCmd(command);

        DefaultExecutor executor = new DefaultExecutor();

        // 忽略错误返回值
        executor.setExitValue(1);
        if (timeout > 0) {
            executor.setWatchdog(new ExecuteWatchdog(TimeUnit.SECONDS.toMillis(timeout)));
        }

        int exitValue = executor.execute(cmdLine);
        return exitValue;
    }

    /**
     * 创建脚本命令
     *
     * @param command
     * @return
     */
    private static CommandLine buildCmd(String command) {
        CommandLine cmdLine;
        if (isWindows) {
            cmdLine = new CommandLine("cmd");
            cmdLine.addArguments("/c");
        } else {
            cmdLine = new CommandLine("sh");
            cmdLine.addArguments("-c");
        }
        cmdLine.addArgument(command, false);
        return cmdLine;
    }

}
