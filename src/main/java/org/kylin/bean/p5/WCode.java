package org.kylin.bean.p5;


import com.google.common.collect.Sets;
import org.apache.commons.collections4.CollectionUtils;
import org.kylin.algorithm.RandomKill;

import java.util.*;

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

    public WCode() {
    }

    public WCode(Integer dim) {
        this.dim = dim ;
        codes = new ArrayList<>();
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

    public WCode copy() throws Exception{
        return (WCode) clone();
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
}
