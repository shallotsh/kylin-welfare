package org.kylin.constant;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum ESIndexEnum {
    /**
     * 开奖结果
     */
    WELFARE_RESULT("welfare_result");
    private String index;

    ESIndexEnum(String index) {
        this.index = index;
    }
}
