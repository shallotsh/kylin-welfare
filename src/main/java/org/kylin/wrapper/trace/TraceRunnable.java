package org.kylin.wrapper.trace;

import org.kylin.util.TraceContext;

import java.util.*;

public class TraceRunnable implements Runnable{

    private final Object trace = TraceContext.getTraceContext();
    private Runnable runnable;

    public TraceRunnable(Runnable runnable) {
        this.runnable = runnable;
    }

    public Runnable getRunnable() {
        return runnable;
    }

    @Override
    public void run() {
        Object backup = TraceContext.backupAndSet(trace);
        try {
            runnable.run();
        } finally {
            TraceContext.restoreBackup(backup);
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
