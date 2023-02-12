package org.kylin.service.xcode.filters.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.kylin.bean.p5.WCode;
import org.kylin.service.xcode.filters.SimpleFilter;
import org.kylin.util.TransferUtil;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class KillAllOddAndEvenFilter implements SimpleFilter{

    @Override
    public List<WCode> filter(List<WCode> target, String filterStr) {

        if(CollectionUtils.isEmpty(target)){
            return Collections.emptyList();
        }
        if(!"true".equalsIgnoreCase(filterStr)){
            return target;
        }
        return target.stream().filter(wCode -> !(wCode.isAllOdd() || wCode.isAllEven())).collect(Collectors.toList());
    }
}
