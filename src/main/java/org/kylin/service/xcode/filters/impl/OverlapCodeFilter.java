package org.kylin.service.xcode.filters.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.kylin.bean.p5.WCode;
import org.kylin.service.xcode.filters.SimpleFilter;
import org.kylin.util.TransferUtil;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 重叠码过滤器
 * 规则：
 * - 输入：一组分隔开的重叠数组码（abc,edf,ghi）
 * - 规律规则：将当前3d库中的每一注3D，mnQ与重叠数组中每一个数字串比较，如果mnQ与重叠数组中有2或3个数字重叠，则保留该3D，否则丢弃。
 */
@Slf4j
public class OverlapCodeFilter implements SimpleFilter {

    @Override
    public List<WCode> filter(List<WCode> target, String filterStr) {
        if(CollectionUtils.isEmpty(target)){
            return Collections.emptyList();
        }
        List<Set<Integer>> overlapCodeList = TransferUtil.toMultiSet(filterStr);

        if(CollectionUtils.isEmpty(overlapCodeList)){
            log.warn("overlap code list is empty. ignore filter.");
            return target;
        }

        return target.stream()
                .filter(code -> overlapCodeList.stream().anyMatch(codeSet -> match(code, codeSet)))
                .collect(Collectors.toList());
    }

    private boolean match(WCode wCode, Set<Integer> dict){
        if(CollectionUtils.isEmpty(dict)){
            return false;
        }
        int count = 0;
        for(Integer num : wCode.getCodes()){
            if(dict.contains(num)){
                count++;
            }
        }
        return count >= 2;
    }



}
