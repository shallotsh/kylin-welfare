package org.kylin.bean.p5;


import com.google.common.base.Joiner;
import com.google.common.collect.Sets;
import org.apache.commons.collections4.CollectionUtils;
import org.kylin.algorithm.RandomKill;
import org.kylin.util.WCodeUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author shallotsh
 * @date 2017/6/30 上午1:42.
 */
public class WCode implements Cloneable,Comparable,RandomKill{
    /**
     * P5码容器，位顺序 万(0)千(1)百(2)十(3)个(4)
     */
    private List<Integer> codes;
    private int dim;
    private int freq;
    private int  sumTail;
    private int classify;
    private boolean deleted;
    private int seqNo;

    private int pair;

    /**
     * 二码和，用于二码字典法
     */
    private int binSumValue;

    public WCode() {
    }

    public WCode(Integer dim) {
        this.dim = dim ;
        codes = new ArrayList<>();
    }

    public WCode(Integer dim, List<Integer> code) {
        Objects.requireNonNull(dim);
        Objects.requireNonNull(CollectionUtils.isNotEmpty(code));
        Objects.requireNonNull(Objects.equals(dim, CollectionUtils.size(code)));
        this.dim = dim ;
        codes = new ArrayList<>(code);
        this.sumTail = sum() % 10;
        this.freq = 0;
    }

    public WCode(int dim, int m, int t, int h, int d, int u){
        codes = new ArrayList<>();
        codes.add(m);
        codes.add(t);
        codes.add(h);
        codes.add(d);
        codes.add(u);
        this.dim = 5;
        this.sumTail = sum() % 10;
        this.freq = 0;
    }


    public WCode(int dim, int h, int d, int u) {
        codes = new ArrayList<>();
        codes.add(h);
        codes.add(d);
        codes.add(u);
        this.dim = dim;
        this.freq = 0;
        int sum = h + d + u;
        this.sumTail = sum % 10;
        this.freq = 0;
    }

    public WCode(int dim, int d, int u) {
        codes = new ArrayList<>();
        codes.add(d);
        codes.add(u);
        this.dim = 2;

        this.freq = 0;

        int sum = d + u;
        this.sumTail = sum % 10;
    }

    public List<Integer> getCodes() {
        return codes;
    }

    public void setCodes(List<Integer> codes) {
        this.codes = codes;
    }

    public int getDim() {
        return dim;
    }

    public void setDim(int dim) {
        this.dim = dim;
    }

    public int getFreq() {
        return freq;
    }

    public WCode setFreq(int freq) {
        this.freq = freq;
        return this;
    }
    public int getSumTail() {
        return sumTail;
    }

    public void setSumTail(int sumTail) {
        this.sumTail = sumTail;
    }

    public void addFreq(int count){
        this.freq += count;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public WCode setDeleted(boolean deleted) {
        this.deleted = deleted;
        return this;
    }


    public int getSeqNo() {
        return seqNo;
    }

    public WCode setSeqNo(int seqNo) {
        this.seqNo = seqNo;
        return this;
    }

    public int getPair() {
        return pair;
    }

    public void setPair(int pair) {
        this.pair = pair;
    }

    public int getBinSumValue() {
        return binSumValue;
    }

    public void setBinSumValue(int binSumValue) {
        this.binSumValue = binSumValue;
    }


    public int codeSum(){
        if(CollectionUtils.isEmpty(codes)){
            return 0;
        }
        return codes.stream().filter(Objects::nonNull).mapToInt(Integer::intValue).sum();
    }


    public String getString(Boolean withFreq, Boolean withSeqNo){

        if(withFreq == null || !withFreq){
            return getString(withSeqNo);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("[" + getFreq() + "]");
        int size = CollectionUtils.size(codes);
        for(int i=0; i<size; i++){
            if(i == 3){
                sb.append("_");
            }
            sb.append(codes.get(i));
        }
        if(seqNo != 0 && withSeqNo){
            sb.append("<"+seqNo+">");
        }
        return sb.toString();
    }

    public String getString(Boolean withSeq){
        StringBuilder sb = new StringBuilder();
        int size = CollectionUtils.size(codes);
        for(int i=0; i<size; i++){
            if(i == 3){
                sb.append("_");
            }
            sb.append(codes.get(i));
        }

        if(seqNo != 0 && withSeq){
            sb.append("<"+seqNo+">");
        }

        return sb.toString();
    }

    public String getStringWithTailSum(){
        return Joiner.on("").join(getCodes()) + "-" + getSumTail();
    }

    public String getStringWithFreqAndTailSum(){
        return "(" + getFreq() + ")" + Joiner.on("").join(getCodes()) + "-" + getSumTail();
    }

    @Override
    public String toString() {
        if(CollectionUtils.isEmpty(codes)){
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append(this.getFreq());
        sb.append("]");
        sb.append(getString(false));
        sb.append("-");
        sb.append(this.sumTail);

        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null || !(obj instanceof WCode)){
            return false;
        }
        if(this == obj){
            return true;
        }

        WCode wCode = (WCode) obj;
        if(CollectionUtils.isEmpty(wCode.getCodes()) || wCode.getDim() != this.getDim()){
            return false;
        }

        for(int i=wCode.getDim()-1; i>=0; i--){
            if(wCode.getCodes().get(i) != this.getCodes().get(i)){
                return false;
            }
        }

        return true;

    }

    @Override
    public int hashCode() {
        int prime = 31;
        int result = 1;

        for(Integer code: this.getCodes()){
            result = prime * result + code;
        }

        return result;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        WCode newObj = (WCode) super.clone();
        newObj.setClassify(this.classify);
        return newObj;
    }

    public WCode copy(){
        try {
            return (WCode) clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public WCode asc(){
        Collections.sort(this.getCodes());
        return this;
    }

    public int sum(){
        return this.getCodes().stream().reduce(0, Integer::sum);
    }

    /**
     * 前三位两码之和
     * @return
     */
    public Set<Integer> sumOfPreThreeBit(){
        Set<Integer> set = new HashSet<>();
        if(dim < 2){
            return set;
        }
        set.add((codes.get(0) + codes.get(1)) % 10);
        if(dim >= 3){
            set.add((codes.get(0) + codes.get(2))%10);
            set.add((codes.get(1) + codes.get(2))%10);
        }

        return set;
    }

    public int getMax(){
        return Collections.max(this.getCodes());
    }

    public int getMin(){
        return Collections.min(this.getCodes());
    }


    public int getClassify() {
        return classify;
    }

    public void setClassify(int classify) {
        this.classify = classify;
    }

    public boolean isInSeq(Set<Integer> seq){
        if(CollectionUtils.isEmpty(seq)){
            return false;
        }

        Set<Integer> codeSet = new HashSet<>(this.getCodes());

        Set<Integer> diff = Sets.difference(codeSet, seq );
        return CollectionUtils.isEmpty(diff);
    }


    public boolean validate(){
        return dim == CollectionUtils.size(this.getCodes());
    }


    /**
     * 计算并返回跨度值
     * @return
     */
    public int getKd(){
        return Math.abs(getMax() - getMin());
    }

    public boolean containsRepeatNumber(){
        Set<Integer> set = new HashSet<>();
        for(Integer e: codes){
            if(set.contains(e)){
                return true;
            }else{
                set.add(e);
            }
        }

        return false;
    }

    public int increaseFreq(){
        this.freq++;
        return this.freq;
    }

    @Override
    public int compareTo(Object o) {
        if(o == null || !(o instanceof WCode)){
            return 1;
        }
        WCode obj = (WCode) o;

        if(this.getFreq() > obj.getFreq()){
            return -1;
        }else if(this.getFreq() < obj.getFreq()){
            return 1;
        }

        for(int i=0; i<this.dim; i++){
            if(this.getCodes().get(i) > obj.getCodes().get(i)){
                return 1;
            }else if(this.getCodes().get(i) < obj.getCodes().get(i)){
                return -1;
            }
        }
        return 0;
    }

    @Override
    public void setBeDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    @Override
    public boolean isBeDeleted() {
        return deleted;
    }

    public boolean equalTo(WCode wCode){
        if(wCode == null || this.codes == null
                || wCode.getCodes() == null || this.getDim() != wCode.getDim()
                || wCode.getPair() != this.getPair()){
            return false;
        }
        for(int i=0; i<wCode.getDim(); i++){
            if(!Objects.equals(wCode.getCodes().get(i), this.getCodes().get(i))){
                return false;
            }
        }
        return true;
    }

    public boolean groupEqual(WCode wCode){
        if(wCode == null || this.codes == null
                || wCode.getCodes() == null || this.getDim() != wCode.getDim()){
            return false;
        }
        List<Integer> list1 = new ArrayList<>(this.getCodes());
        List<Integer> list2 = new ArrayList<>(wCode.getCodes());
        Collections.sort(list1);
        Collections.sort(list2);

        for(int i=0; i<list1.size(); i++){
            if(!Objects.equals(list1.get(i),list2.get(i))){
                return false;
            }
        }
        return true;
    }

    public boolean isAllOdd(){
        for (Integer code : getCodes()){
            if(code != null && (code % 2 == 0)){
                return false;
            }
        }
        return true;
    }

    public boolean isAllEven(){
        for (Integer code : getCodes()){
            if(code != null && (code % 2 != 0)){
                return false;
            }
        }
        return true;
    }

    public static int compareByTailNo(WCode o1, WCode o2){
            if(o1.getSumTail() != o2.getSumTail()){
                if(o1.getSumTail() > o2.getSumTail()){
                    return 1;
                }else{
                    return -1;
                }
            }else{
                return o1.compareTo(o2);
            }
    }
}
