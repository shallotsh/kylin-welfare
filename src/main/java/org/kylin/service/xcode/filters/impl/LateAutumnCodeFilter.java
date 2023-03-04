package org.kylin.service.xcode.filters.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.kylin.bean.p5.WCode;
import org.kylin.service.xcode.filters.SimpleFilter;
import org.kylin.util.TransferUtil;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 晚秋选码，当前仅用于复式选码
 * v1:晚秋选码是将输入的四组数与1390先后对比，如四组数中任意一组含有1390的2个数或2个数以上则留下1390，否则去掉1390
 * v2:(当前) 将四码组选码mnop与输入的几组数如abcde比对，若mnop含有abcde中的3个或4个相同数字，则将mnop这组四码组去掉
 */
public class LateAutumnCodeFilter implements SimpleFilter{

    private static final Integer SPECIAL_CODE_DIM = 4;

    @Override
    public List<WCode> filter(List<WCode> target, String filterStr) {

        if(CollectionUtils.isEmpty(target) || target.get(0).getDim() != SPECIAL_CODE_DIM){
            return target;
        }

        List<Set<Integer>> lateAutumnCodeSets = TransferUtil.toMultiSet(filterStr);

        // 如果为空，则直接返回
        if(CollectionUtils.isEmpty(lateAutumnCodeSets)){
            return target;
        }

        List<WCode> ret = target.stream().filter(
                wCode -> meetFishManCondition(lateAutumnCodeSets, wCode)
        ).collect(Collectors.toList());

        return ret;
    }


    private boolean meetFishManCondition(List<Set<Integer>> lateAutumnCodeSets, WCode wCode){
        if(wCode == null || CollectionUtils.isEmpty(lateAutumnCodeSets)){
            return false;
        }
        for(Set<Integer> set : lateAutumnCodeSets){
            int count = 0;
            for(Integer code : wCode.getCodes()){
                if(set.contains(code)){
                    count++;
                }
            }
            if(count > 2){
                return false;
            }
        }
        return true;
    }
}
