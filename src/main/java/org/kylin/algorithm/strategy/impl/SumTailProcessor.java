package org.kylin.algorithm.strategy.impl;

import com.google.common.collect.Sets;
import org.apache.commons.collections4.CollectionUtils;
import org.kylin.algorithm.strategy.SequenceProcessor;
import org.kylin.bean.p5.WCode;
import org.kylin.bean.p5.WCodeReq;
import org.kylin.util.TransferUtil;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SumTailProcessor implements SequenceProcessor {
    private List<WCode> wCodes;
    private Set<Integer> sumTailSet;


    @Override
    public SequenceProcessor init(WCodeReq wCodeReq) {
        if(wCodeReq != null){
           wCodes = wCodeReq.getWCodes();
            sumTailSet = TransferUtil.toIntegerSet(wCodeReq.getBoldCodeFive());
        }
        return this;
    }

    @Override
    public List<WCode> process(List<WCode> deletedCodes) {
        if(!validate()){
            return wCodes;
        }

        List<WCode> ret = wCodes.stream().filter(wCode -> !Sets.intersection(wCode.sumOfPreThreeBit(), sumTailSet).isEmpty()).collect(Collectors.toList());

        return ret;
    }

    @Override
    public boolean validate() {
        if(CollectionUtils.isEmpty(wCodes) || CollectionUtils.isEmpty(sumTailSet)){
            return false;
        }
        return true;
    }
}
