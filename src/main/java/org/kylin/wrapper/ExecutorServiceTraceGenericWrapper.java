package org.kylin.wrapper;

import org.kylin.wrapper.trace.TraceCallable;
import org.kylin.wrapper.trace.TraceRunnable;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

public class ExecutorServiceTraceGenericWrapper<T extends ExecutorService> implements ExecutorService,ScheduledExecutorService {

    private T executorService;

    public ExecutorServiceTraceGenericWrapper(T executorService) {
        this.executorService = executorService;
    }

    public T get(){
        return executorService;
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return executorService.submit(TraceCallable.get(task));
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        return executorService.submit(TraceRunnable.get(task), result);
    }

    @Override
    public Future<?> submit(Runnable task) {
        return executorService.submit(TraceRunnable.get(task));
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return executorService.invokeAll(TraceCallable.gets(tasks));
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        return executorService.invokeAll(TraceCallable.gets(tasks), timeout, unit);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        return null;
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return null;
    }

    @Override
    public void execute(Runnable command) {
        executorService.execute(TraceRunnable.get(command));
    }


    @Override
    public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {

        if(executorService instanceof ScheduledExecutorService){
            return ((ScheduledExecutorService) executorService).schedule(TraceRunnable.get(command), delay, unit);
        }

        throw new RuntimeException("不支持的操作");
    }

    @Override
    public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
        if(executorService instanceof ScheduledExecutorService){
            return ((ScheduledExecutorService) executorService).schedule(TraceCallable.get(callable), delay, unit);
        }

        throw new RuntimeException("不支持的操作");
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
        if(executorService instanceof ScheduledExecutorService){
            return ((ScheduledExecutorService) executorService).scheduleAtFixedRate(TraceRunnable.get(command),initialDelay, period, unit);
        }

        throw new RuntimeException("不支持的操作");
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
        if(executorService instanceof ScheduledExecutorService){
            return ((ScheduledExecutorService) executorService).scheduleWithFixedDelay(TraceRunnable.get(command),initialDelay, delay, unit);
        }

        throw new RuntimeException("不支持的操作");

    }

    @Override
    public void shutdown() {
        executorService.shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        return executorService.shutdownNow();
    }

    @Override
    public boolean isShutdown() {
        return executorService.isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return executorService.isTerminated();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return executorService.awaitTermination(timeout, unit);
    }
}
