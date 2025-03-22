package org.kylin.service.xcode.filters.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.kylin.bean.p5.WCode;
import org.kylin.service.xcode.filters.SimpleFilter;
import org.kylin.util.TransferUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class StrictSeqKillFilter implements SimpleFilter {

    @Override
    public List<WCode> filter(List<WCode> target, String filterStr) {
        if(CollectionUtils.isEmpty(target)
                || StringUtils.isBlank(filterStr)){
            return target;
        }
        List<List<Integer>> filterLists = TransferUtil.toMultiList(filterStr);
        if(CollectionUtils.isEmpty(filterLists)){
            return target;
        }

        return target.stream()
                .filter(w -> filterByCommonSequence(w, filterLists))
                .collect(Collectors.toList());
    }

    private boolean filterByCommonSequence(WCode wCode, List<List<Integer>> dicts){

        List<Integer> codes = wCode.getCodes();
        List<Pair<Integer,Integer>> pairs = new ArrayList<>();
        pairs.add(Pair.of(codes.get(0), codes.get(1)));
        pairs.add(Pair.of(codes.get(1), codes.get(2)));

        return dicts.stream().anyMatch(dict -> pairs.stream().anyMatch(pair -> contains(dict, pair)));
    }

    private boolean contains(List<Integer> list, Pair<Integer, Integer> pair){
        if(Objects.equals(pair.getLeft(), pair.getRight())){
            return list.indexOf(pair.getLeft()) != list.lastIndexOf(pair.getRight());
        }
        int leftIndex = list.indexOf(pair.getLeft());
        return leftIndex != -1 && list.lastIndexOf(pair.getRight()) > leftIndex;
    }




}
