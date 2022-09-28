package com.adrninistrator.jacg.runner;

import com.adrninistrator.jacg.common.enums.OtherConfigFileUseSetEnum;
import com.adrninistrator.jacg.conf.ConfInfo;
import com.adrninistrator.jacg.conf.ConfInfoly;
import com.adrninistrator.jacg.conf.ConfManager;
import com.adrninistrator.jacg.conf.ConfigureWrapper;
import com.adrninistrator.jacg.dboper.DbOperator;
import com.adrninistrator.jacg.util.JACGUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author liyue
 * @date 2022//9
 * @description: 从数据库读取数据，生成指定类调用的所有向下的调用关系，支持忽略特定的包名、类、方法
 */

public class RunnerGenAllGraph4CallerSupportIgnorely extends RunnerGenAllGraph4CallerSupportIgnore {
    private static final Logger logger = LoggerFactory.getLogger(RunnerGenAllGraph4CallerSupportIgnorely.class);
    private Map<String, Set<String>> otherConfigSetMap = new HashMap<>();

    /**
     * 执行任务
     *
     * @return true: 成功；false: 失败
     */
    public boolean run() {
        long startTime = System.currentTimeMillis();
        someTaskFail = false;
        if (confInfo == null) {
            return false;
        }
        //设置其他配置（入口方法们
        //待写 暂时写在cocotest---<外面
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

    public void setConfInfo(ConfInfo confInfo) {
        this.confInfo = confInfo;
    }

    // 读取配置文件中指定的需要处理的任务
    protected boolean readTaskInfo(OtherConfigFileUseSetEnum otherConfigFileUseSetEnum) {
        if (otherConfigSetMap.get(otherConfigFileUseSetEnum.getFileName()) != null) {
            taskSet = otherConfigSetMap.get(otherConfigFileUseSetEnum.getFileName());
        } else {
            taskSet = ConfigureWrapper.getOtherConfigSet(otherConfigFileUseSetEnum);
        }
        if (JACGUtil.isCollectionEmpty(taskSet)) {
            logger.error("读取文件不存在或内容为空 {}", otherConfigFileUseSetEnum.getFileName());
            return false;
        }

        return true;
    }

    public Map<String, String> getMethodInConfAndFileMap() {
        return this.methodInConfAndFileMap;
    }

    public Map<String, Set<String>> getOtherConfigSetMap() {
        return this.otherConfigSetMap;
    }

    public void addOtherConfigSet(OtherConfigFileUseSetEnum otherConfigFileUseSetEnum, Set<String> configSet) {
        if (configSet == null) {
            return;
        }
        this.otherConfigSetMap.put(otherConfigFileUseSetEnum.getFileName(), configSet);
    }

    public Map<String, String> getSimpleClassNameMap() {
        return this.simpleClassNameMap;
    }
}
