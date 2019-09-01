package org.kylin.exception;

import lombok.Getter;

public enum AppExceptionEnum {
    ACQUIRE_DISTRIBUTED_LOCK_EXCEPTION("DISTRIBUTED_LOCK_EXCEPTION", "获取分布式锁异常"),
    ACQUIRE_DISTRIBUTED_LOCK_TIMEOUT("DISTRIBUTED_LOCK_TIMEOUT", "获取锁超时"),
    ;
    @Getter
    private String code;
    @Getter
    private String desc;

    AppExceptionEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
