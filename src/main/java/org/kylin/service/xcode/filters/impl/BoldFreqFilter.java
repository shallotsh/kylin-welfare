package org.kylin.service.xcode.filters.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.kylin.bean.p5.WCode;
import org.kylin.service.xcode.filters.SimpleFilter;
import org.kylin.util.TransferUtil;
import org.kylin.util.WCodeUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 胆频杀: 胆频选指含3D的频度，次数。二码和选是二码和出现的次数。
 * 如输入数组3567  1467  6789  0234四组数，对3D集中的398进行比对，除1467中不含398外，其余3组都含，则留下398存入N
 *
 */
public class BoldFreqFilter implements SimpleFilter {

    @Override
    public List<WCode> filter(List<WCode> target, String filterStr) {
        if(CollectionUtils.isEmpty(target)){
            return Collections.emptyList();
        }
        List<Set<Integer>> dicts = TransferUtil.toMultiSet(filterStr);
        List<List<WCode>> wCodesArray = new ArrayList<>();
        for(Set<Integer> boldFreqValues: dicts){
            wCodesArray.add(filterByBoldFreq(target, boldFreqValues));
        }
        return mergeAndFilterWCodesByMinFreq(wCodesArray, 3);
    }


    private List<WCode> filterByBoldFreq(List<WCode> source, Set<Integer> binFreqValues){
        if(CollectionUtils.isEmpty(source) || CollectionUtils.isEmpty(binFreqValues)){
            return Collections.emptyList();
        }
        // 单组胆频码进行过滤
        List<WCode> copyCodes =  new ArrayList<>(source);
        Iterator<WCode> iterator = copyCodes.iterator();
        while(iterator.hasNext()){
            WCode code = iterator.next();
            if(code.getCodes().stream().anyMatch(x -> binFreqValues.contains(x))){
                continue;
            }
            iterator.remove();
        }
        return copyCodes;
    }


    /**
     * 合并 & 设置频次
     *
     * @param wCodesArray
     * @param minFreqInclude
     * @return
     */
    private List<WCode> mergeAndFilterWCodesByMinFreq(List<List<WCode>> wCodesArray, int minFreqInclude){
        // 合并 & 设置频次 & 过滤频次
        List<WCode> ret = WCodeUtils.mergeCodes(wCodesArray.stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList()), true);

        return ret.stream().filter(x -> x.getFreq() >= minFreqInclude).collect(Collectors.toList());
    }



}
