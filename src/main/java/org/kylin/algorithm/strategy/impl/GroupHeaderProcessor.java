package org.kylin.algorithm.strategy.impl;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.kylin.algorithm.strategy.SequenceProcessor;
import org.kylin.bean.p5.WCode;
import org.kylin.bean.p5.WCodeReq;
import org.kylin.util.TransferUtil;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

public class GroupHeaderProcessor implements SequenceProcessor {

    private Integer groupSize = 13;
    private Set<Integer> columnIdxs = new HashSet<>(Arrays.asList(0));
    private List<WCode> wCodes;

    @Override
    public SequenceProcessor init(WCodeReq wCodeReq) {
        if(StringUtils.isNotBlank(wCodeReq.getBoldCodeFive())){
            columnIdxs = TransferUtil.parseSet(wCodeReq.getBoldCodeFive());
            columnIdxs = columnIdxs.stream().map(column -> column - 1).collect(Collectors.toSet());
        }

        wCodes = wCodeReq.getwCodes();

        return this;
    }

    @Override
    public List<WCode> process(List<WCode> deletedCodes) {
        if(!validate()){
            return wCodes;
        }

        List<WCode> ret = new ArrayList<>();

        List<List<WCode>> codesArray = Lists.partition(wCodes, groupSize);
        for(List<WCode> wCodeList : codesArray){
            for(int i=0; i<wCodeList.size(); i++){
                if(columnIdxs.contains(i)){
                    ret.add(wCodeList.get(i));
                }
            }
        }

        return ret;
    }

    @Override
    public boolean validate() {
        if(CollectionUtils.isEmpty(wCodes) ){
            return false;
        }

        return true;
    }
}
