package org.kylin.service.xcode.filters.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.kylin.bean.p5.WCode;
import org.kylin.service.xcode.filters.SimpleFilter;
import org.kylin.util.TransferUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 晚秋选码，当前仅用于复式选码
 * v1:晚秋选码是将输入的四组数与1390先后对比，如四组数中任意一组含有1390的2个数或2个数以上则留下1390，否则去掉1390
 * v2:(当前) 将四码组选码mnop与输入的几组数如abcde比对，若mnop含有abcde中的3个或4个相同数字，则将mnop这组四码组去掉
 * v2.1(已取消，并回退到v2): 复式组选法中进行晚秋选玛时如出现有重码的情况，如0155，则请定义为5只算一个，即如往框中输入025，则0155要保留，而不是去掉。原定义中含3，4个则去掉，这里有重复者只算含了一个
 * v3: 晚秋杀码后需要保留被杀码，用于最后导出。在输出中增加一个杀四码（即通过晚秋选码去掉的四码也单独输出），同时增加一个余四码，即初四码减去杀四码后的结果
 *
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

//        List<WCode> ret =
//                target.stream().filter(
//                wCode -> meetFishManCondition(lateAutumnCodeSets, wCode)
//        ).collect(Collectors.toList());

        target.forEach(wCode -> {
            if(!meetFishManCondition(lateAutumnCodeSets, wCode)){
                wCode.setDeleted(true);
            }
        });

        return target;
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
