package org.kylin.util;

import org.apache.commons.lang3.RandomStringUtils;
import org.kylin.constant.Constants;
import org.slf4j.MDC;

import java.util.Objects;

public final class TraceContext {

    private TraceContext() {
    }

    public static Object getTraceContext(){
        String traceId = MDC.get(Constants.LOG_TRACE_ID);
        if(Objects.isNull(traceId)){
            return String.valueOf(System.currentTimeMillis() / 1000 + RandomStringUtils.randomNumeric(4));
        }

        return traceId;
    }


    public static Object backupAndSet(Object obj){
        String backup = MDC.get(Constants.LOG_TRACE_ID);
        MDC.put(Constants.LOG_TRACE_ID, obj.toString());
        return backup;
    }

    public static void restoreBackup(Object obj){
        if(Objects.nonNull(obj)) {
            MDC.put(Constants.LOG_TRACE_ID, obj.toString());
        }else{
            MDC.remove(Constants.LOG_TRACE_ID);
        }
    }

}
