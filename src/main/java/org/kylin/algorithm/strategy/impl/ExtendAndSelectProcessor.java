package org.kylin.algorithm.strategy.impl;

import com.google.common.collect.Lists;
import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.kylin.algorithm.strategy.SequenceProcessor;
import org.kylin.bean.p5.WCode;
import org.kylin.bean.p5.WCodeReq;
import org.kylin.util.WCodeUtils;

import java.util.List;
import java.util.Optional;
import java.util.Random;


@Slf4j
public class ExtendAndSelectProcessor implements SequenceProcessor {

    private List<WCode> wCodes;
    private int extendRatio;
    private int selectCount;

    @Override
    public SequenceProcessor init(WCodeReq wCodeReq) {
        if(wCodeReq != null && StringUtils.isNotBlank(wCodeReq.getBoldCodeFive())) {
            wCodes = wCodeReq.getWCodes();

//            Optional<Pair<Integer, Integer>> extendAndSelectCount = parseValuePair(wCodeReq.getBoldCodeFive());

//            if(extendAndSelectCount.isPresent()){
//                extendRatio = extendAndSelectCount.get().getKey();
//                selectCount = extendAndSelectCount.get().getValue();
//            }

            extendRatio = wCodeReq.getExtendRatio();
            selectCount = NumberUtils.toInt(wCodeReq.getBoldCodeFive());
        }
        return this;
    }

    private Optional<Pair<Integer, Integer>> parseValuePair(String seq){
        if(StringUtils.isBlank(seq)){
            return Optional.empty();
        }

        String[] vals = seq.split("#|$|@|,|/| |-");
        if(vals == null || vals.length <2){
            log.info("扩库码解析错误");
            return Optional.empty();
        }

        if(!StringUtils.isNumeric(vals[0]) || !StringUtils.isNumeric(vals[1])){
            log.info("扩库码解析错误");
            return Optional.empty();
        }

        return Optional.of(new Pair<>(NumberUtils.toInt(vals[0]), NumberUtils.toInt(vals[1])));
    }


    @Override
    public List<WCode> process(List<WCode> deletedCodes) {
        if(!validate()){
            return wCodes;
        }


        int expectedCodeNum = wCodes.size() * extendRatio;

        int count = 0;
        List<WCode> randomSelected = Lists.newArrayListWithCapacity(selectCount);

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
                log.error("发生异常", e);
            }
        }

        int randomSize = ret.size();

        while(count < selectCount && count < ret.size()){
            int index = new Random().nextInt(randomSize);

            try {
                randomSelected.add(ret.get(index).copy());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            count += 1;
        }

        return randomSelected;
    }

    @Override
    public boolean validate() {
        return CollectionUtils.isNotEmpty(wCodes) && extendRatio > 0 && extendRatio <= 100 && selectCount > 0;
    }
}
