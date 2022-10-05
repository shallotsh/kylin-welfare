package org.kylin.service.xcode.filters.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.kylin.bean.p3.BitUnitDTO;
import org.kylin.bean.p5.WCode;
import org.kylin.service.xcode.filters.CompositeFilter;
import org.kylin.service.xcode.filters.CompositeFilterDTO;
import org.kylin.util.TransferUtil;
import org.kylin.util.WCodeUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BitUnitCompositeFilter implements CompositeFilter {

    @Override
    public List<WCode> filter(List<WCode> target, CompositeFilterDTO param) {
        if(!(param instanceof BitUnitDTO)){
            log.error("位选-复合过滤器参数类型不匹配");
            return target;
        }
        BitUnitDTO bitUnitDTO = (BitUnitDTO) param;
        if(!bitUnitDTO.isValid()){
            log.error("位选-复合过滤器参数错误");
            return target;
        }

        return process(target, bitUnitDTO);
    }

    // 执行位选逻辑
    private List<WCode> process(List<WCode> target, BitUnitDTO bitUnitDTO){
        List<WCode> hundredFilterRet = killByBit(target, 0, bitUnitDTO.getHundredSeq());
        List<WCode> decadeFilterRet = killByBit(target, 1, bitUnitDTO.getDecadeSeq());
        List<WCode> unitFilterRet = killByBit(target, 2, bitUnitDTO.getUnitSeq());

        List<WCode> ret = WCodeUtils.mergeCodes(Arrays.asList(hundredFilterRet, decadeFilterRet, unitFilterRet).stream()
        .flatMap(x -> x.stream()).collect(Collectors.toList()));

        List<WCode> deletedCodes = WCodeUtils.minus(target, ret);
        if(CollectionUtils.isNotEmpty(deletedCodes)){
            deletedCodes.forEach(code -> {
                code.setFreq(0);
                code.setDeleted(true);
            });
        }

        ret.addAll(deletedCodes);

        return ret;
    }


    private List<WCode> killByBit(List<WCode> target, int bitIndex, String bitSeq){
        List<WCode> copyCodes = new ArrayList<>(target);
        Set<Integer> bitSet = TransferUtil.toIntegerSet(bitSeq);
        Iterator<WCode> iterator = copyCodes.iterator();
        while(iterator.hasNext()){
            WCode wCode = iterator.next();
            if(!bitSet.contains(wCode.getCodes().get(bitIndex))){
                iterator.remove();
            }
        }
        return copyCodes;
    }


}
