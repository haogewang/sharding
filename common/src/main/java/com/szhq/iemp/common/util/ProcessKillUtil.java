package com.szhq.iemp.common.util;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.management.ManagementFactory;

/**
 * @author wanghao
 * @date 2019/12/16
 */
@Slf4j
public class ProcessKillUtil {

    public static String getCurrentPid(){
        String name = ManagementFactory.getRuntimeMXBean().getName();
        log.error("process name:" + name);
        String pid = name.split("@")[0];
        return pid;
    }

    public static void killProcessByPid(String pid) {
        try {
            Runtime.getRuntime().exec("kill -9 " + pid);
        } catch (IOException e) {
           log.error("e", e);
        }
    }

}
