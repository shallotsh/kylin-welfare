package org.kylin.bean.p5;

import org.kylin.bean.LabelValue;

import java.util.List;
import java.util.Map;

public class WCodeSummarise {
    private List<WCode> randomKillCodes;
    private List<WCode> wCodes;
    private List<WCode> backupCodes;
    private Map<String, List<WCode>> deletedCodesPair;
    private Integer pairCodes;
    private Integer nonPairCodes;
    private boolean isRandomKill;
    private Integer remainedCodesCount;
    private boolean freqSeted;
    private Integer extendCount;
    private Integer extendRatio;

    private List<LabelValue<List<WCode>>> deletedCodes;

    public List<WCode> getRandomKillCodes() {
        return randomKillCodes;
    }

    public WCodeSummarise setRandomKillCodes(List<WCode> randomKillCodes) {
        this.randomKillCodes = randomKillCodes;
        return this;
    }

    public List<WCode> getwCodes() {
        return wCodes;
    }

    public WCodeSummarise setwCodes(List<WCode> wCodes) {
        this.wCodes = wCodes;
        return this;
    }

    public List<WCode> getBackupCodes() {
        return backupCodes;
    }

    public WCodeSummarise setBackupCodes(List<WCode> backupCodes) {
        this.backupCodes = backupCodes;
        return this;
    }

    public Integer getPairCodes() {
        return pairCodes;
    }

    public WCodeSummarise setPairCodes(Integer pairCodes) {
        this.pairCodes = pairCodes;
        return this;
    }


    public Integer getNonPairCodes() {
        return nonPairCodes;
    }

    public WCodeSummarise setNonPairCodes(Integer nonPairCodes) {
        this.nonPairCodes = nonPairCodes;
        return this;
    }

    public boolean isRandomKill() {
        return isRandomKill;
    }

    public WCodeSummarise setRandomKill(boolean randomKill) {
        isRandomKill = randomKill;
        return this;
    }

    public Integer getRemainedCodesCount() {
        return remainedCodesCount;
    }

    public WCodeSummarise setRemainedCodesCount(Integer remainedCodesCount) {
        this.remainedCodesCount = remainedCodesCount;
        return this;
    }

    public boolean isFreqSeted() {
        return freqSeted;
    }

    public WCodeSummarise setFreqSeted(boolean freqSeted) {
        this.freqSeted = freqSeted;
        return this;
    }

    public Integer getExtendCount() {
        return extendCount;
    }

    public WCodeSummarise setExtendCount(Integer extendCount) {
        this.extendCount = extendCount;
        return this;
    }

    public Integer getExtendRatio() {
        return extendRatio;
    }

    public WCodeSummarise setExtendRatio(Integer extendRatio) {
        this.extendRatio = extendRatio;
        return this;
    }

    public List<LabelValue<List<WCode>>> getDeletedCodes() {
        return deletedCodes;
    }

    public WCodeSummarise setDeletedCodes(List<LabelValue<List<WCode>>> deletedCodes) {
        this.deletedCodes = deletedCodes;
        return this;
    }
}
