package org.kylin.wrapper.trace;

import org.kylin.constant.Constants;
import org.slf4j.MDC;

import java.util.*;

public class TraceRunnable implements Runnable{

    private final String traceId = MDC.get(Constants.LOG_TRACE_ID);
    private Runnable runnable;

    public TraceRunnable(Runnable runnable) {
        this.runnable = runnable;
    }

    public Runnable getRunnable() {
        return runnable;
    }

    @Override
    public void run() {
        String backTrace = MDC.get(Constants.LOG_TRACE_ID);
        MDC.put(Constants.LOG_TRACE_ID, traceId);

        try {
            runnable.run();
        } finally {
            MDC.put(Constants.LOG_TRACE_ID, backTrace);
        }

    }

    public static TraceRunnable get(Runnable runnable) {
        if (runnable == null) {
            return null;
        } else {
            return runnable instanceof TraceRunnable ? (TraceRunnable)runnable : new TraceRunnable(runnable);
        }
    }


    public static List<TraceRunnable> gets(Collection<? extends Runnable> tasks){

        if(Objects.isNull(tasks)){
            return Collections.emptyList();
        }

        List<TraceRunnable> copy = new ArrayList<>();
        for(Runnable task : tasks){
            copy.add(get(task));
        }

        return copy;
    }

}
