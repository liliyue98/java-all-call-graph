package com.adrninistrator.jacg.conf;

import com.adrninistrator.jacg.common.enums.ConfigKeyEnum;

/**
 * @author liyue
 * @date 2022/9/15
 * @description:
 */
public class ConfInfoly {

    public static ConfInfo getConfInfo(ConfInfoFromCC confInfoFromCC) {
        ConfInfo confInfo = ConfInfoClone.myclone(ConfManager.getConfInfo());
        confInfo.setAppName(confInfoFromCC.getAppName());
        confInfo.setCallGraphJarList(confInfoFromCC.getCallGraphJarList());
        if (confInfoFromCC.isInputIgnoreOtherPackage() != null) {
            confInfo.setInputIgnoreOtherPackage(confInfoFromCC.isInputIgnoreOtherPackage());
        }
        if (confInfoFromCC.isGenCombinedOutput() != null) {
            confInfo.setGenCombinedOutput(confInfoFromCC.isGenCombinedOutput());
        }
        if (confInfoFromCC.isIgnoreDupCalleeInOneCaller() != null) {
            confInfo.setIgnoreDupCalleeInOneCaller(confInfoFromCC.isIgnoreDupCalleeInOneCaller());
        }
        if (confInfoFromCC.getCallGraphOutputDetail() != null) {
            confInfo.setCallGraphOutputDetail(confInfoFromCC.getCallGraphOutputDetail());
        }
        if (confInfoFromCC.isShowMethodAnnotation() != null) {
            confInfo.setShowMethodAnnotation(confInfoFromCC.isShowMethodAnnotation());
        }
        if (confInfoFromCC.getDbH2FilePath() != null) {
            confInfo.setDbH2FilePath(confInfoFromCC.getDbH2FilePath());
        }
        return confInfo;
    }

    //初始化参数
    public static String appName = "currentProject";
    public static String callGraphJarList = "target/classes";
    public static String CKE_INPUT_IGNORE_OTHER_PACKAGE = "false";
    public static String CCKE_GEN_COMBINED_OUTPUT = "true";
    public static String CKE_SHOW_CALLER_LINE_NUM = "true";
    public static String CKE_IGNORE_DUP_CALLEE_IN_ONE_CALLER = "false";
    public static String CKE_MULTI_IMPL_GEN_IN_CURRENT_FILE = "false";
    public static String CKE_CALL_GRAPH_OUTPUT_DETAIL = "1";
    public static String CKE_THREAD_NUM = "20";
    public static String CKE_SHOW_METHOD_ANNOTATION = "true";
    public static String CKE_DB_USE_H2 = "true";
    public static String CKE_DB_H2_FILE_PATH = "./build/jacg_h2db";

    public static void setConfigMap() {
        //添加必要配置
//        ConfigureWrapper.addConfig(ConfigKeyEnum.CKE_APPNAME, appName);
//        ConfigureWrapper.addConfig(ConfigKeyEnum.CKE_CALL_GRAPH_JAR_LIST, ConfInfoly.callGraphJarList);
//        ConfigureWrapper.addConfig(ConfigKeyEnum.CKE_INPUT_IGNORE_OTHER_PACKAGE, ConfInfoly.CKE_INPUT_IGNORE_OTHER_PACKAGE);
//        ConfigureWrapper.addConfig(ConfigKeyEnum.CKE_GEN_COMBINED_OUTPUT, ConfInfoly.CCKE_GEN_COMBINED_OUTPUT);
        ConfigureWrapper.addConfig(ConfigKeyEnum.CKE_SHOW_CALLER_LINE_NUM, ConfInfoly.CKE_SHOW_CALLER_LINE_NUM);
//        ConfigureWrapper.addConfig(ConfigKeyEnum.CKE_IGNORE_DUP_CALLEE_IN_ONE_CALLER, ConfInfoly.CKE_IGNORE_DUP_CALLEE_IN_ONE_CALLER);
        ConfigureWrapper.addConfig(ConfigKeyEnum.CKE_MULTI_IMPL_GEN_IN_CURRENT_FILE, ConfInfoly.CKE_MULTI_IMPL_GEN_IN_CURRENT_FILE);
//        ConfigureWrapper.addConfig(ConfigKeyEnum.CKE_CALL_GRAPH_OUTPUT_DETAIL, ConfInfoly.CKE_CALL_GRAPH_OUTPUT_DETAIL);
//        ConfigureWrapper.addConfig(ConfigKeyEnum.CKE_THREAD_NUM, ConfInfoly.CKE_THREAD_NUM);
//        ConfigureWrapper.addConfig(ConfigKeyEnum.CKE_SHOW_METHOD_ANNOTATION, ConfInfoly.CKE_SHOW_METHOD_ANNOTATION);
        ConfigureWrapper.addConfig(ConfigKeyEnum.CKE_DB_USE_H2, ConfInfoly.CKE_DB_USE_H2);
//        ConfigureWrapper.addConfig(ConfigKeyEnum.CKE_DB_H2_FILE_PATH, ConfInfoly.CKE_DB_H2_FILE_PATH);
    }
}
