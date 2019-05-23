package org.kylin.config;

import org.kylin.wrapper.ExecutorServiceTraceGenericWrapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.Executors;

@Configuration
public class ScheduleConfig implements SchedulingConfigurer {

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(new ExecutorServiceTraceGenericWrapper<>(Executors.newScheduledThreadPool(2)));
    }
}
