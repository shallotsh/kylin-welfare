package org.kylin.algorithm.strategy.impl;

import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.kylin.algorithm.strategy.SequenceProcessor;
import org.kylin.bean.p5.WCode;
import org.kylin.bean.p5.WCodeReq;

import java.util.List;
import java.util.Random;

public class RandomSelectProcessor implements SequenceProcessor {

    private List<WCode> wCodes;
    private int randomNum;

    @Override
    public SequenceProcessor init(WCodeReq wCodeReq) {
        if(wCodeReq != null) {
            wCodes = wCodeReq.getWCodes();
            randomNum = NumberUtils.toInt(wCodeReq.getBoldCodeFive());
        }
        return this;
    }

    @Override
    public List<WCode> process(List<WCode> deletedCodes) {
        int count = 0;
        List<WCode> randomSelected = Lists.newArrayListWithCapacity(randomNum);
        while(randomNum > 0 && CollectionUtils.size(wCodes) > count){
            int randomSize = wCodes.size();
            int index = new Random().nextInt(randomSize);

            randomSelected.add(wCodes.get(index));
            randomNum -= 1;
        }

        return randomSelected;
    }

    @Override
    public boolean validate() {
        return CollectionUtils.isNotEmpty(wCodes) && randomNum > 0;
    }
}
