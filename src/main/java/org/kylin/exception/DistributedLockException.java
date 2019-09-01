package org.kylin.exception;

public class DistributedLockException extends BaseException{

    public DistributedLockException(AppExceptionEnum appExceptionEnum) {
        super(appExceptionEnum);
    }

    public DistributedLockException(String message, AppExceptionEnum appExceptionEnum) {
        super(message, appExceptionEnum);
    }

    public DistributedLockException(String message, Throwable cause, AppExceptionEnum appExceptionEnum) {
        super(message, cause, appExceptionEnum);
    }

    public DistributedLockException(Throwable cause, AppExceptionEnum appExceptionEnum) {
        super(cause, appExceptionEnum);
    }
}
