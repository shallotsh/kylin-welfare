package org.kylin.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Optional;

@AllArgsConstructor
@Getter
public enum ExportPatternEnum {
    NORMAL(0, "常规导出", null),

    LOCATION_2D(201, "定位2D导出", null),

    EXPERT_3D(204, "专家推荐法", null),

    BIN_SUM_FREQ_3D(205, "3D二和频度法", null),

    WCODE_4D(206, "复式组选", null),

    BIN_SUM_DICT_3D(207, "二码字典法", null),

    WCODE_4D_TO_3D(208, "复式组选·转三码", null),

    TWO_DERIVE_THREE_CODE(209, "我要发·二组三码法", null),

    ;

    private int id;
    private String desc;
    private String title;



    public static Optional<ExportPatternEnum> getById(int id){
        for(ExportPatternEnum exportPatternEnum : ExportPatternEnum.values()){
            if(exportPatternEnum.getId() == id){
                return Optional.of(exportPatternEnum);
            }
        }

        return Optional.empty();
    }
}
