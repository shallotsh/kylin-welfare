package org.kylin.service.xcode.filters.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.kylin.bean.p5.WCode;
import org.kylin.service.xcode.filters.SimpleFilter;
import org.kylin.util.TransferUtil;
import org.kylin.util.WCodeUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class InverseSelectCodeFilter implements SimpleFilter{

    @Override
    public List<WCode> filter(List<WCode> target, String filterStr) {
        if(CollectionUtils.isEmpty(target)){
            return Collections.emptyList();
        }

        List<Pair<Integer,Integer>> pairs = TransferUtil.parsePairCodeList(filterStr);
        if(CollectionUtils.isEmpty(pairs)){
            return target;
        }

        List<WCode> ret = target.stream().filter(
                wCode -> !WCodeUtils.isEqualAnyInPairCodeInOrder(wCode, pairs)
        ).collect(Collectors.toList());

        return ret;
    }


}
