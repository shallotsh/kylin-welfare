package org.kylin.exception;

import lombok.Getter;

public class BaseException extends Exception{

    @Getter
    private AppExceptionEnum appExceptionEnum;

    public BaseException(AppExceptionEnum appExceptionEnum) {
        this.appExceptionEnum = appExceptionEnum;
    }

    public BaseException(String message, AppExceptionEnum appExceptionEnum) {
        super(message);
        this.appExceptionEnum = appExceptionEnum;
    }

    public BaseException(String message, Throwable cause, AppExceptionEnum appExceptionEnum) {
        super(message, cause);
        this.appExceptionEnum = appExceptionEnum;
    }

    public BaseException(Throwable cause, AppExceptionEnum appExceptionEnum) {
        super(cause);
        this.appExceptionEnum = appExceptionEnum;
    }

    public String getCode(){
        return this.getAppExceptionEnum().getCode();
    }
}
