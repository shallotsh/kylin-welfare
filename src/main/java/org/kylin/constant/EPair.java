package org.kylin.constant;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum EPair {
    UNSET(0, "未设置"),
    NON_PAIR(1,"非对子"),
    PAIR(2,"对子"),
    ;
    int code;
    String desc;

    EPair(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
