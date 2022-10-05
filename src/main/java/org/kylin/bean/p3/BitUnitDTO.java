package org.kylin.bean.p3;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.kylin.service.xcode.filters.CompositeFilterDTO;

@Data
public class BitUnitDTO implements CompositeFilterDTO {
    private String hundredSeq;
    private String decadeSeq;
    private String unitSeq;

    public boolean isValid(){
        if( StringUtils.isBlank(hundredSeq)
                || StringUtils.isBlank(decadeSeq)
                || StringUtils.isBlank(unitSeq)){
            return false;
        }
        // todo 其他校验
        return true;
    }


}
