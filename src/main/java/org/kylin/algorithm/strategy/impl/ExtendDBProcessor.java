package org.kylin.algorithm.strategy.impl;

import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.kylin.algorithm.strategy.SequenceProcessor;
import org.kylin.bean.p5.WCode;
import org.kylin.bean.p5.WCodeReq;

import java.util.List;


public class ExtendDBProcessor implements SequenceProcessor {

    private List<WCode> wCodes;
    private int extendRatio;

    @Override
    public SequenceProcessor init(WCodeReq wCodeReq) {
        if(wCodeReq != null) {
            wCodes = wCodeReq.getWCodes();
            extendRatio = NumberUtils.toInt(wCodeReq.getBoldCodeFive());
        }
        return this;
    }

    @Override
    public List<WCode> process(List<WCode> deletedCodes) {
        if(!validate()){
            return wCodes;
        }

        int expectedCodeNum = wCodes.size() * extendRatio;
        List<WCode> ret = Lists.newArrayListWithExpectedSize(expectedCodeNum);
        for(WCode wCode: wCodes){
            for(int i=0; i<extendRatio; i++){
                try {
                    ret.add(wCode.copy());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return ret;
    }

    @Override
    public boolean validate() {
        return CollectionUtils.isNotEmpty(wCodes) && extendRatio > 0 && extendRatio < 100;
    }
}
