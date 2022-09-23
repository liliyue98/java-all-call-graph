package com.adrninistrator.jacg.runner;

import com.adrninistrator.jacg.common.JACGConstants;
import com.adrninistrator.jacg.common.enums.ConfigKeyEnum;
import com.adrninistrator.jacg.conf.*;
import com.adrninistrator.jacg.dboper.DbOperator;
import com.adrninistrator.jacg.dto.annotation.AnnotationInfo4Write;
import com.adrninistrator.jacg.runner.base.AbstractRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liyue
 * @date 2022/9/6
 * @description: 从数据库读取数据，生成指定类调用的所有向下的调用关系，支持忽略特定的包名、类、方法
 */

public class RunnerWriteDbly extends RunnerWriteDb {
    private static final Logger logger = LoggerFactory.getLogger(RunnerWriteDbly.class);
    protected static RunnerWriteDbly runner1;
    private static boolean inited = false;

    static {
        runner1 = new RunnerWriteDbly();
    }

    public static void main(String[] args) {
        ConfInfoFromCC confInfoFromCC = new ConfInfoFromCC();
        confInfoFromCC.setAppName("test_2022090510");
        confInfoFromCC.setCallGraphJarList("C:\\Users\\hasee\\Desktop\\call0.77\\modeCocotest\\target\\modeCocotest-1.0-SNAPSHOT.jar");
        runner1.run(confInfoFromCC);
    }


    /**
     * 执行任务
     *
     * @return true: 成功；false: 失败
     */
    public boolean run(ConfInfoFromCC confInfoFromCC) {
        long startTime = System.currentTimeMillis();
        someTaskFail = false;
        //先初始化一个ConfigureWrapper
        if (!inited) {
            ConfInfoly.setConfigMap();
            inited = true;
        }
        confInfo = ConfInfoly.getConfInfo(confInfoFromCC);

        //未传入最关键的两个值直接返回false
        if (confInfo.getAppName() == null || confInfo.getCallGraphJarList() == null) {
            logger.error("未传入appName或callGraphList", this.getClass().getSimpleName());
            return false;
        }

        if (!preCheck()) {
            logger.error("{} 预检查失败", this.getClass().getSimpleName());
            return false;
        }

        dbOperator = DbOperator.getInstance();
        if (!dbOperator.init(confInfo)) {
            return false;
        }

        if (!init()) {
            logger.error("{} 初始化失败", this.getClass().getSimpleName());
            return false;
        }

        operate();

        beforeExit();

        long spendTime = System.currentTimeMillis() - startTime;
        logger.info("{} 耗时: {} s", this.getClass().getSimpleName(), spendTime / 1000.0D);

        return !someTaskFail;
    }

    public ConfInfo getConfInfo() {
        return this.confInfo;
    }

}
