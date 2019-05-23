package org.kylin.wrapper.trace;

import org.kylin.util.TraceContext;

import java.util.*;
import java.util.concurrent.Callable;

public class TraceCallable<V> implements Callable<V> {

    private final Object trace = TraceContext.getTraceContext();

    private Callable<V> callable;

    public TraceCallable(Callable<V> callable) {
        this.callable = callable;
    }

    public Callable<V> getCallable() {
        return callable;
    }

    @Override
    public V call() throws Exception {

        Object backup = TraceContext.backupAndSet(trace);

        try {
            return callable.call();
        } finally {
            TraceContext.restoreBackup(backup);
        }
    }

    public static<T> TraceCallable<T> get(Callable<T> callable) {
        if (callable == null) {
            return null;
        } else {
            return callable instanceof TraceCallable ? (TraceCallable<T>) callable : new TraceCallable<>(callable);
        }
    }


    public static<T> List<TraceCallable<T>> gets(Collection<? extends Callable<T>> tasks){

        if(Objects.isNull(tasks)){
            return Collections.emptyList();
        }

        List<TraceCallable<T>> copy = new ArrayList<>();
        for(Callable<T> task : tasks){
            copy.add(get(task));
        }

        return copy;
    }
}
