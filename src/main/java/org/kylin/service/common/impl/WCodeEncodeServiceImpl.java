package org.kylin.service.common.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.kylin.bean.p5.WCode;
import org.kylin.service.common.IWCodeEncodeService;
import org.kylin.util.WCodeUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@Service
public class WCodeEncodeServiceImpl implements IWCodeEncodeService {

    @Override
    public List<WCode> compositionEncode(List<Set<Integer>> riddles) {
        if(CollectionUtils.isEmpty(riddles) || riddles.size() != 3) {
            throw new RuntimeException("不支持的编码");
        }

        List<WCode> wCodes = new ArrayList<>();

        for(Integer e1: riddles.get(0)){
            for(Integer e2: riddles.get(1)){
                for(Integer e3: riddles.get(2)){
                    wCodes.add(new WCode(3, e1, e2, e3));
                }
            }
        }

        return WCodeUtils.convertToGroup(wCodes);
    }
}
