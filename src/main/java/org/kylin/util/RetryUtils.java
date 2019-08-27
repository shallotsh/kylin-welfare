package org.kylin.util;

import com.github.rholder.retry.*;
import lombok.extern.slf4j.Slf4j;
import org.kylin.exception.NeedRetryExcetpion;

import java.util.concurrent.TimeUnit;

@Slf4j
public class RetryUtils {


    public static<T> Retryer<T> build(Long waitTimeMilli, int retryTimes) {
        return RetryerBuilder.<T>newBuilder()
                .retryIfExceptionOfType(NeedRetryExcetpion.class)
                .withWaitStrategy(WaitStrategies.fixedWait(waitTimeMilli, TimeUnit.MILLISECONDS))
                .withStopStrategy(StopStrategies.stopAfterAttempt(retryTimes))
                .build();
    }

}
