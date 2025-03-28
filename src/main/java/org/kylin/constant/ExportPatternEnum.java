package org.kylin.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Optional;

@AllArgsConstructor
@Getter
public enum ExportPatternEnum {
    NORMAL(0, "常规导出", null),
    BIG_SUM_KILL(2, "大和杀导出", null),
    CONTAIN_THREE_KILL(4, "含三杀导出", null),
    CONTAIN_FOUR_KILL(5, "含四杀导出", null),
    CONTAIN_FIVE_KILL(6, "含五杀导出", null),

    DELETED_CODES(7, "删除码导出", null),
    DELETED_HAS_SAME_BIT_CODES(8, "重删除码导出", null),

    HALF_PAGE(100, "半页导出", "《我要发·排列5》福彩3D预测(半页)"),
    NORMAL_SEQ_NO(101, "常规带序号导出", null),

    LOCATION_2D(201, "定位2D导出", null),
    GROUP_COLUMN(202, "组首输出", null),
    P5_SELECT_3D(203, "P5取3D导出", null),

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
