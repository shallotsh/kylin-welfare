package org.kylin.service.xcode.filters.impl;

import com.google.common.collect.Sets;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.kylin.bean.p5.WCode;
import org.kylin.service.xcode.filters.SimpleFilter;
import org.kylin.util.TransferUtil;
import org.kylin.util.WCodeUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 二码和杀
 *
 * 输入二码和数组 abcd，与当前3d库中的3d之两两和比对，如有重叠的数字，则留下此3D，繁殖则去掉此3d
 * 例如输入和尾 2346 与3d码 298（和尾017）无重叠数字，则将298从3d库中移除
 *
 */
public class TwoCodeSumFilter implements SimpleFilter {

    @Override
    public List<WCode> filter(List<WCode> target, String filterStr) {
        if(CollectionUtils.isEmpty(target) || StringUtils.isBlank(filterStr)){
            return target;
        }
        List<Integer> twoCodeSumSeq = TransferUtil.toIntegerList(filterStr);

        return target.stream().filter(code -> filterByTwoCodeSumSeq(code, twoCodeSumSeq)).collect(Collectors.toList());
    }


    /**
     * by二码和过滤
     *
     * @param wCode
     * @param twoCodeSumSeq
     * @return true, 符合条件；false，不符合条件
     */
    private boolean filterByTwoCodeSumSeq(WCode wCode, List<Integer> twoCodeSumSeq){
        List<Integer> codes = wCode.getCodes();
        List<Pair<Integer,Integer>> pairs = new ArrayList<>();
        pairs.add(Pair.of(codes.get(0), codes.get(1)));
        pairs.add(Pair.of(codes.get(1), codes.get(2)));
        pairs.add(Pair.of(codes.get(0), codes.get(2)));

        for(Pair<Integer, Integer> pair : pairs){
            if(twoCodeSumSeq.contains((pair.getLeft() + pair.getRight())%10)){
                return true;
            }
        }
        return false;
    }



}
