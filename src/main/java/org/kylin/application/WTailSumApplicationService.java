package org.kylin.application;

import org.apache.commons.collections4.CollectionUtils;
import org.kylin.bean.TailSumValue;
import org.kylin.bean.p5.WCode;
import org.kylin.service.common.IWCodeEncodeService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WTailSumApplicationService {
    @Resource
    private IWCodeEncodeService iwCodeEncodeService;

    public TailSumValue computeTailSumValueByComposition(List<Integer> riddleSet){

        if(CollectionUtils.isEmpty(riddleSet)){
            return new TailSumValue();
        }
        List<Integer> distinctRiddle = riddleSet.stream().distinct().collect(Collectors.toList());
        List<WCode> w2dCodes = iwCodeEncodeService.combine4Code(distinctRiddle, 2);
        List<Integer> w2dTailSumValues = w2dCodes.stream().map(WCode::codeSum).distinct().sorted().collect(Collectors.toList());

        List<WCode> w3dCodes = iwCodeEncodeService.combine4Code(distinctRiddle, 3);
        List<Integer> w3dTailSumValues = w3dCodes.stream().map(WCode::codeSum).distinct().sorted().collect(Collectors.toList());

        return new TailSumValue(w2dTailSumValues, w3dTailSumValues);
    }


}
