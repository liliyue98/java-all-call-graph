package com.adrninistrator.jacg.conf;

/**
 * @author adrninistrator
 * @date 2021/6/17
 * @description:
 */
public class ConfInfoFromCC {


    //自定义初始化的参数(必传)
    private String appName;
    private String callGraphJarList;
    //非必须
    private Boolean inputIgnoreOtherPackage;
    private Boolean genCombinedOutput;
    private Boolean ignoreDupCalleeInOneCaller;
    private String callGraphOutputDetail;
    private Boolean showMethodAnnotation;
    private String dbH2FilePath;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getCallGraphJarList() {
        return callGraphJarList;
    }

    public void setCallGraphJarList(String callGraphJarList) {
        this.callGraphJarList = callGraphJarList;
    }

    public Boolean isInputIgnoreOtherPackage() {
        return inputIgnoreOtherPackage;
    }

    public void setInputIgnoreOtherPackage(Boolean inputIgnoreOtherPackage) {
        this.inputIgnoreOtherPackage = inputIgnoreOtherPackage;
    }

    public Boolean isGenCombinedOutput() {
        return genCombinedOutput;
    }

    public void setGenCombinedOutput(Boolean genCombinedOutput) {
        this.genCombinedOutput = genCombinedOutput;
    }

    public Boolean isIgnoreDupCalleeInOneCaller() {
        return ignoreDupCalleeInOneCaller;
    }

    public void setIgnoreDupCalleeInOneCaller(Boolean ignoreDupCalleeInOneCaller) {
        this.ignoreDupCalleeInOneCaller = ignoreDupCalleeInOneCaller;
    }

    public String getCallGraphOutputDetail() {
        return callGraphOutputDetail;
    }

    public void setCallGraphOutputDetail(String callGraphOutputDetail) {
        this.callGraphOutputDetail = callGraphOutputDetail;
    }

    public Boolean isShowMethodAnnotation() {
        return showMethodAnnotation;
    }

    public void setShowMethodAnnotation(Boolean showMethodAnnotation) {
        this.showMethodAnnotation = showMethodAnnotation;
    }

    public String getDbH2FilePath() {
        return dbH2FilePath;
    }

    public void setDbH2FilePath(String dbH2FilePath) {
        this.dbH2FilePath = dbH2FilePath;
    }
}
