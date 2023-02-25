package org.kylin.constant;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum EW4DClassify {
    FOUR_DECOMPOSE(11, "四码分解"),
    FOUR_PAIR_CODE(12, "对子"),
    FOUR_NON_PAIR_CODE(13, "非对子"),
    ;
    private Integer id;
    private String desc;

    EW4DClassify(Integer id, String desc) {
        this.id = id;
        this.desc = desc;
    }

    public static EW4DClassify getById(Integer id){
        for(EW4DClassify classify : EW4DClassify.values()){
            if(classify.getId().equals(id)){
                return classify;
            }
        }
        return null;
    }
}
