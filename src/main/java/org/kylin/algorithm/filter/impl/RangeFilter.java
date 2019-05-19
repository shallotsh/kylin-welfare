package org.kylin.algorithm.filter.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.kylin.algorithm.filter.CodeFilter;
import org.kylin.bean.FilterParam;
import org.kylin.bean.W3DCode;
import org.kylin.bean.WelfareCode;
import org.kylin.util.TransferUtil;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 *  跨度杀码
 * @author huangyawu
 * @date 2017/7/23 下午3:13.
 */
@Service
public class RangeFilter implements CodeFilter<WelfareCode> {

    @Override
    public void filter(WelfareCode code, FilterParam filterParam) {
        if(code == null ||
                CollectionUtils.isEmpty(code.getW3DCodes()) ||
                filterParam == null ||
                StringUtils.isBlank(filterParam.getRange())){
            return;
        }

        Set<Integer> rangeSet = TransferUtil.toIntegerSet(filterParam.getRange());

        if(CollectionUtils.isEmpty(rangeSet)){
            return;
        }

        List<W3DCode> w3DCodes = code.getW3DCodes();

        Iterator<W3DCode> iterator = w3DCodes.iterator();
        while(iterator.hasNext()){
            W3DCode w3DCode = iterator.next();
            int range = w3DCode.getMax() - w3DCode.getMin();
            if(!rangeSet.contains(range)) {
                iterator.remove();
            }
        }

        code.setW3DCodes(w3DCodes);
    }
}
