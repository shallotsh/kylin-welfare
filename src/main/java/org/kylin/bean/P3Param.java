package org.kylin.bean;

import lombok.Data;

public class P3Param {
    private String condition;
    private WelfareCode welfareCode;

    private String abSeq;
    private String bcSeq;
    private String acSeq;

    private Integer extendRatio;
    private Integer extendSelectCount;

    public String getCondition() {
        return condition;
    }

    public P3Param setCondition(String condition) {
        this.condition = condition;
        return this;
    }

    public WelfareCode getWelfareCode() {
        return welfareCode;
    }

    public P3Param setWelfareCode(WelfareCode welfareCode) {
        this.welfareCode = welfareCode;
        return this;
    }

    public String getAbSeq() {
        return abSeq;
    }

    public P3Param setAbSeq(String abSeq) {
        this.abSeq = abSeq;
        return this;
    }

    public String getBcSeq() {
        return bcSeq;
    }

    public P3Param setBcSeq(String bcSeq) {
        this.bcSeq = bcSeq;
        return this;
    }

    public String getAcSeq() {
        return acSeq;
    }

    public P3Param setAcSeq(String acSeq) {
        this.acSeq = acSeq;
        return this;
    }

    public Integer getExtendRatio() {
        return extendRatio;
    }

    public P3Param setExtendRatio(Integer extendRatio) {
        this.extendRatio = extendRatio;
        return this;
    }

    public Integer getExtendSelectCount() {
        return extendSelectCount;
    }

    public P3Param setExtendSelectCount(Integer extendSelectCount) {
        this.extendSelectCount = extendSelectCount;
        return this;
    }

    @Override
    public String toString() {
        return "P3Param{" +
                "condition='" + condition + '\'' +
                ", welfareCode=" + welfareCode +
                ", abSeq='" + abSeq + '\'' +
                ", bcSeq='" + bcSeq + '\'' +
                ", acSeq='" + acSeq + '\'' +
                ", extendRatio=" + extendRatio +
                ", extendSelectCount=" + extendSelectCount +
                '}';
    }
}
