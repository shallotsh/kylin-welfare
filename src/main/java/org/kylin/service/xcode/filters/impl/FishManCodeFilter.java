package org.kylin.service.xcode.filters.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.kylin.bean.p5.WCode;
import org.kylin.service.xcode.filters.SimpleFilter;
import org.kylin.util.TransferUtil;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 钓叟选码，当前仅用于复式选码
 * 输入一组数abcdef，将其与库中的每组四码复式相比较，有三个数字或者4个数字重叠，则将该四码留下，否则去掉。
 */
public class FishManCodeFilter implements SimpleFilter{

    private static final Integer SPECIAL_CODE_DIM = 4;

    @Override
    public List<WCode> filter(List<WCode> target, String filterStr) {

        if(CollectionUtils.isEmpty(target) || target.get(0).getDim() != SPECIAL_CODE_DIM){
            return target;
        }

        Set<Integer> fishCodeSet = TransferUtil.toIntegerSet(filterStr);

        if(CollectionUtils.isEmpty(fishCodeSet)){
            return target;
        }

        List<WCode> ret = target.stream().filter(
                wCode -> meetFishManCondition(fishCodeSet, wCode)
        ).collect(Collectors.toList());

        return ret;
    }


    private boolean meetFishManCondition(Set<Integer> fishCodeSet, WCode wCode){
        if(wCode == null || CollectionUtils.isEmpty(fishCodeSet)){
            return false;
        }
        return wCode.getCodes().stream().filter( x -> fishCodeSet.contains(x)).count() >= 3;
    }
}
