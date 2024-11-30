package org.kylin.service.xcode.filters.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.kylin.bean.p5.WCode;
import org.kylin.service.xcode.filters.SimpleFilter;
import org.kylin.util.TransferUtil;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 三码杀：输入数组abcd....与集合中的每个3D码比对，如果此3D码中的3个数字全部包含在数组中，则该3D码不能出现在结果中。
 */
public class TripleCodeKillFilter implements SimpleFilter {

    @Override
    public List<WCode> filter(List<WCode> target, String filterStr) {
        if(CollectionUtils.isEmpty(target)){
            return Collections.emptyList();
        }

        Set<Integer> tripeSet = TransferUtil.toIntegerSet(filterStr);

        if(CollectionUtils.isEmpty(tripeSet)){
            return target;
        }

        return target.stream().filter(wCode -> !containsAll(wCode, tripeSet)).collect(Collectors.toList());
    }

    private boolean containsAll(WCode wCode, Set<Integer> tripeSet){
        for(int code : wCode.getCodes()){
            if(!tripeSet.contains(code)){
                return false;
            }
        }
        return true;
    }
}
