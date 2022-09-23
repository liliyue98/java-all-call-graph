package com.adrninistrator.jacg.runner;

import com.adrninistrator.jacg.common.enums.OtherConfigFileUseSetEnum;
import com.adrninistrator.jacg.conf.*;
import com.adrninistrator.jacg.runner.base.AbstractRunner;

import java.util.HashSet;
import java.util.Set;

public class RunnerTest {
    private static RunnerTest runner;

    static {
        runner = new RunnerTest();
    }

    public static void main(String[] args) {
        runner.run();
    }

    public void run() {
        ConfInfoFromCC confInfoFromCC = new ConfInfoFromCC();
        confInfoFromCC.setCallGraphJarList("C:\\Users\\hasee\\Desktop\\call0.75\\output_dir\\jar\\run_jacg.jar");
        confInfoFromCC.setAppName("testcallerly");
        //缺乏对上述两变量的校验
        RunnerWriteDbly runnerWriteDbly = new RunnerWriteDbly();
        Boolean writeDB = runnerWriteDbly.run(confInfoFromCC);
        System.out.println(writeDB);
        if (writeDB) {
            RunnerGenAllGraph4CallerSupportIgnorely runnerGenAllGraph4CallerSupportIgnorely = new RunnerGenAllGraph4CallerSupportIgnorely();
            runnerGenAllGraph4CallerSupportIgnorely.setConfInfo(runnerWriteDbly.getConfInfo());
            Set<String> analyze = new HashSet<>();
            analyze.add("com.adrninistrator.jacg.runner.RunnerWriteDb:run()");
            runnerGenAllGraph4CallerSupportIgnorely.addOtherConfigSet(OtherConfigFileUseSetEnum.OCFUSE_OUT_GRAPH_FOR_CALLER_ENTRY_METHOD, analyze);
            Boolean callGraph = runnerGenAllGraph4CallerSupportIgnorely.run();
        }
    }
}
