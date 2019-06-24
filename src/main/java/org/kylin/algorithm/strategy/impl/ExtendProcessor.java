package org.kylin.algorithm.strategy.impl;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.kylin.algorithm.strategy.SequenceProcessor;
import org.kylin.bean.p5.WCode;
import org.kylin.bean.p5.WCodeReq;
import org.kylin.util.WCodeUtils;

import java.util.List;

@Slf4j
public class ExtendProcessor implements SequenceProcessor {

    private List<WCode> wCodes;
    private int extendRatio;

    @Override
    public SequenceProcessor init(WCodeReq wCodeReq) {
        if(wCodeReq != null && StringUtils.isNotBlank(wCodeReq.getBoldCodeFive())) {
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

        List<WCode> ret = Lists.newArrayListWithCapacity(expectedCodeNum);
        int idx = 1;
        for(WCode wCode : wCodes){
            try {
                if(WCodeUtils.isPair(wCode)){
                    ret.add(wCode.copy().setSeqNo(idx++));
                    continue;
                }
                for(int i=0; i<extendRatio; i++){
                    ret.add(wCode.copy().setSeqNo(idx++));
                }
            } catch (Exception e) {
                log.error("扩库发生异常", e);
            }
        }

        return ret;
    }

    @Override
    public boolean validate() {
        return CollectionUtils.isNotEmpty(wCodes) && extendRatio > 0 && extendRatio <= 10000 ;
    }
}
