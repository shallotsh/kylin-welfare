package org.kylin.bean;

import lombok.ToString;


/**
 * @author huangyawu
 * @date 2017/6/25 下午3:39.
 */
@ToString
public class FilterParam {

    private String sumValue;
    private String boldCode;
    private String gossip;
    private String range;
    private String ternaryLocation;
    private String fishMan;
    private String orderFishMan;
    private String huBits;
    private String hBits;
    private String dBits;
    private String uBits;

    private Boolean isKillDipolar;
    private Integer dipolar;
    private Boolean isKillOneEnd;
    private Integer oneEnd;
    private Boolean isKillBigSum;
    private Integer bigSum;
    private Boolean isKillAllOddEven;
    private Integer oddEven;

    private Integer abc;
    private Integer acb;
    private Integer bac;
    private Integer bca;
    private Integer cab;
    private Integer cba;

    private String freqs;

    private WelfareCode welfareCode;

    private Integer randomKillCount;
    private Boolean randomKilled;

    public String getSumValue() {
        return sumValue;
    }

    public void setSumValue(String sumValue) {
        this.sumValue = sumValue;
    }

    public String getBoldCode() {
        return boldCode;
    }

    public void setBoldCode(String boldCode) {
        this.boldCode = boldCode;
    }

    public String getGossip() {
        return gossip;
    }

    public void setGossip(String gossip) {
        this.gossip = gossip;
    }

    public String getRange() {
        return range;
    }

    public void setRange(String range) {
        this.range = range;
    }

    public String getTernaryLocation() {
        return ternaryLocation;
    }

    public void setTernaryLocation(String ternaryLocation) {
        this.ternaryLocation = ternaryLocation;
    }

    public String getFishMan() {
        return fishMan;
    }

    public void setFishMan(String fishMan) {
        this.fishMan = fishMan;
    }

    public String getOrderFishMan() {
        return orderFishMan;
    }

    public FilterParam setOrderFishMan(String orderFishMan) {
        this.orderFishMan = orderFishMan;
        return this;
    }

    public String getHuBits() {
        return huBits;
    }

    public void setHuBits(String huBits) {
        this.huBits = huBits;
    }

    public String gethBits() {
        return hBits;
    }

    public void sethBits(String hBits) {
        this.hBits = hBits;
    }

    public String getdBits() {
        return dBits;
    }

    public void setdBits(String dBits) {
        this.dBits = dBits;
    }

    public String getuBits() {
        return uBits;
    }

    public void setuBits(String uBits) {
        this.uBits = uBits;
    }

    public Boolean getKillDipolar() {
        return isKillDipolar ;
    }

    public void setKillDipolar(Boolean killDipolar) {
        isKillDipolar = killDipolar;
    }

    public Integer getDipolar() {
        return dipolar;
    }

    public void setDipolar(Integer dipolar) {
        this.dipolar = dipolar;
        if(dipolar != null && dipolar == 1){
            isKillDipolar = true;
        }
    }

    public Boolean getKillOneEnd() {
        return isKillOneEnd ;
    }

    public void setKillOneEnd(Boolean killOneEnd) {
        isKillOneEnd = killOneEnd;
    }

    public Integer getOneEnd() {
        return oneEnd;
    }

    public void setOneEnd(Integer oneEnd) {
        this.oneEnd = oneEnd;
        if(oneEnd != null && oneEnd == 1){
            isKillAllOddEven = true;
        }
    }

    public Boolean getKillBigSum() {
        return isKillBigSum;
    }

    public void setKillBigSum(Boolean killBigSum) {
        isKillBigSum = killBigSum;
    }

    public Integer getBigSum() {
        return bigSum;
    }

    public void setBigSum(Integer bigSum) {
        this.bigSum = bigSum;
        if (bigSum != null && bigSum == 1){
            isKillBigSum = true;
        }
    }

    public Boolean getKillAllOddEven() {
        return isKillAllOddEven ;
    }

    public void setKillAllOddEven(Boolean killAllOddEven) {
        isKillAllOddEven = killAllOddEven;
    }

    public Integer getOddEven() {
        return oddEven;
    }

    public void setOddEven(Integer oddEven) {
        this.oddEven = oddEven;
        if (oddEven != null && oddEven == 1){
            isKillAllOddEven = true;
        }
    }

    public String getFreqs() {
        return freqs;
    }

    public void setFreqs(String freqs) {
        this.freqs = freqs;
    }

    public WelfareCode getWelfareCode() {
        return welfareCode;
    }

    public void setWelfareCode(WelfareCode welfareCode) {
        this.welfareCode = welfareCode;
    }

    public Integer getRandomKillCount() {
        return randomKillCount;
    }

    public FilterParam setRandomKillCount(Integer randomKillCount) {
        this.randomKillCount = randomKillCount;
        return this;
    }

    public Boolean getRandomKilled() {
        return randomKilled;
    }

    public FilterParam setRandomKilled(Boolean randomKilled) {
        this.randomKilled = randomKilled;
        return this;
    }

    public Integer getAbc() {
        return abc;
    }

    public FilterParam setAbc(Integer abc) {
        this.abc = abc;
        return this;
    }

    public Integer getAcb() {
        return acb;
    }

    public FilterParam setAcb(Integer acb) {
        this.acb = acb;
        return this;
    }

    public Integer getBac() {
        return bac;
    }

    public FilterParam setBac(Integer bac) {
        this.bac = bac;
        return this;
    }

    public Integer getBca() {
        return bca;
    }

    public FilterParam setBca(Integer bca) {
        this.bca = bca;
        return this;
    }

    public Integer getCab() {
        return cab;
    }

    public FilterParam setCab(Integer cab) {
        this.cab = cab;
        return this;
    }

    public Integer getCba() {
        return cba;
    }

    public FilterParam setCba(Integer cba) {
        this.cba = cba;
        return this;
    }
}
