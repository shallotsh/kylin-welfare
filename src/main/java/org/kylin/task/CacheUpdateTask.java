package org.kylin.task;

import lombok.extern.slf4j.Slf4j;
import org.kylin.bean.sd.SdDrawNoticeResult;
import org.kylin.util.OkHttpUtils;
import org.kylin.wrapper.GuavaCacheWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Configuration
@EnableScheduling
@Slf4j
public class CacheUpdateTask {

    @Autowired
    private GuavaCacheWrapper cacheWrapper;

    @Scheduled(cron = "0 15,20,25,30,35 * * * ?")
    public void updateTask(){

        Optional<SdDrawNoticeResult> retOpt = OkHttpUtils.getSdDrawNoticeResult("3d", 1);
        if(!retOpt.isPresent()){
            return;
        }

        String key = "3d1";
        cacheWrapper.invalidate(key);
        cacheWrapper.put(key, retOpt.get());

        log.info("更新缓存完成");
    }

}
