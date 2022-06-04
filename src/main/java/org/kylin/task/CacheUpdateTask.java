package org.kylin.task;

import lombok.extern.slf4j.Slf4j;
import org.kylin.bean.sd.SdDrawNoticeResult;
import org.kylin.util.MyDateUtil;
import org.kylin.util.OkHttpUtils;
import org.kylin.wrapper.GuavaCacheWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDate;
import java.util.Optional;

@Configuration
@EnableScheduling
@Slf4j
public class CacheUpdateTask {

    @Autowired
    private GuavaCacheWrapper<SdDrawNoticeResult> cacheWrapper;

    @Scheduled(cron = "0 15,20,25,30,35 * * * ?")
    public void updateTask(){


        LocalDate drawDate = MyDateUtil.getLatestDrawDate();
        String key = "3D_"+drawDate;

        Optional<SdDrawNoticeResult> retOpt = OkHttpUtils.getSdDrawNoticeResult(drawDate, drawDate);

        retOpt.ifPresent(ret -> {
            cacheWrapper.invalidate(key);
            cacheWrapper.put(key, retOpt.get());
            log.info("更新缓存完成");
        });
    }

}
