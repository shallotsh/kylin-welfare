package org.kylin.service.xcode.filters.impl;

import com.google.common.collect.Sets;
import org.apache.commons.collections4.CollectionUtils;
import org.kylin.bean.p5.WCode;
import org.kylin.service.xcode.filters.SimpleFilter;
import org.kylin.util.TransferUtil;
import org.kylin.util.WCodeUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 二和尾杀
 * 1.针对每一组二和尾：
 *  abc的二码和定义为a＋b，a＋c ，b＋c
 *  对子aab的二码和定义为a＋a，a＋b，a＋a＋b
 *   即每个3D码都有3个二码和与之对应
 *   如A：234（二玛和567），911（二玛和012），输入数组123，比对A，则911留下
 *      因012中含有12
 * 2.合并不同组二和尾，统计频次。
 *
 */
public class BinSumFilter implements SimpleFilter {

    @Override
    public List<WCode> filter(List<WCode> target, String filterStr) {
        if(CollectionUtils.isEmpty(target)){
            return Collections.emptyList();
        }
        List<Set<Integer>> dicts = TransferUtil.toMultiSet(filterStr);
        List<List<WCode>> wCodesArray = new ArrayList<>();
        for(Set<Integer> binSumValues: dicts){
            wCodesArray.add(filterByBinSum(target, binSumValues));
        }
        return mergeWCodes(wCodesArray);
    }


    private List<WCode> filterByBinSum(List<WCode> source, Set<Integer> binSumValues){
        // 单个二和码进行过滤
        List<WCode> copyCodes =  new ArrayList<>(source);
        Iterator<WCode> iterator = copyCodes.iterator();
        while(iterator.hasNext()){
            Set<Integer> binSumOfCode = WCodeUtils.calcBinSumsOf3D(iterator.next());
            if(CollectionUtils.isEmpty(Sets.intersection(binSumOfCode, binSumValues))){
                iterator.remove();
            }
        }
        return copyCodes;
    }


    /**
     * 合并 & 设置频次
     *
     * @param wCodesArray
     * @return
     */
    private List<WCode> mergeWCodes(List<List<WCode>> wCodesArray){
        // 合并 & 设置频次
        List<WCode> ret = WCodeUtils.mergeCodes(wCodesArray.stream()
                .flatMap(x -> x.stream()).collect(Collectors.toList()));

        return ret;
    }



}
