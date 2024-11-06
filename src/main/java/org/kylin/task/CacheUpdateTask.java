package org.kylin.task;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections4.CollectionUtils;
import org.kylin.bean.sd.SdDrawNoticeResult;
import org.kylin.constant.ESIndexEnum;
import org.kylin.util.MyDateUtil;
import org.kylin.util.OkHttpUtils;
import org.kylin.wrapper.ESWrapper;
import org.kylin.wrapper.GuavaCacheWrapper;
import org.kylin.wrapper.bo.SdDrawResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.Optional;

@Configuration
@EnableScheduling
@Slf4j
public class CacheUpdateTask {

    @Autowired
    private GuavaCacheWrapper<SdDrawNoticeResult> cacheWrapper;

    @Resource
    private ESWrapper esWrapper;

    @Scheduled(cron = "0 15,16,20,30 21,22,23 * * ?")
    public void updateTask(){

        LocalDate drawDate = MyDateUtil.getLatestDrawDate();
        String key = "3D_"+drawDate;

        Optional<SdDrawNoticeResult> retOpt = OkHttpUtils.getSdDrawNoticeResult(drawDate, drawDate);

        retOpt.ifPresent(ret -> {
            if(CollectionUtils.isEmpty(ret.getResult())) {
                log.info("查询结果为空");
                return ;
            }
            cacheWrapper.invalidate(key);
            cacheWrapper.put(key, retOpt.get());
            log.info("更新缓存完成");

            // 记录结果
            SdDrawResult res = SdDrawResult.from(ret.getResult().get(0));
            if(!esWrapper.exists(ESIndexEnum.WELFARE_RESULT.getIndex(), res.getCode())) {
                esWrapper.index(ESIndexEnum.WELFARE_RESULT.getIndex(), res.getCode(), res);
            }
        });
    }

}
