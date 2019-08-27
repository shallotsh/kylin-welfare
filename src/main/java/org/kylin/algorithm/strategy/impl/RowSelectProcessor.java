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

/**
 * 行以7个行，根据输入的行数选出目标P5码
 */
public class RowSelectProcessor implements SequenceProcessor {

    private Integer groupSize = 7;
    private Integer rowSize = 13;
    private Set<Integer> rowIdxs = new HashSet<>(Arrays.asList(0));
    private List<WCode> wCodes;

    @Override
    public SequenceProcessor init(WCodeReq wCodeReq) {
        if(StringUtils.isNotBlank(wCodeReq.getBoldCodeFive())){
            rowIdxs = TransferUtil.parseSet(wCodeReq.getBoldCodeFive());
            rowIdxs = rowIdxs.stream().map(column -> column - 1).collect(Collectors.toSet());
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

        List<List<WCode>> codesArray = Lists.partition(wCodes, rowSize);

        List<List<List<WCode>>> codesArrs = Lists.partition(codesArray, groupSize);

        for(int i = 0; i<codesArrs.size(); i++){
            if(rowIdxs.contains(i)){
                for(List<WCode> wCodeList : codesArrs.get(i)) {
                    ret.addAll(wCodeList);
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
