package org.kylin.api;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.kylin.bean.WyfDataResponse;
import org.kylin.bean.WyfErrorResponse;
import org.kylin.bean.WyfResponse;
import org.kylin.bean.sd.SdDrawNoticeResult;
import org.kylin.util.OkHttpUtils;
import org.kylin.wrapper.GuavaCacheWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/3d")
@Slf4j
public class SdDrawApiController {



    @Autowired
    private GuavaCacheWrapper<SdDrawNoticeResult> cacheWrapper;

    @RequestMapping(value = "/draw/notice", method = RequestMethod.GET)
    public WyfResponse findDrawNotice(String name, Integer issueCount){

        if(StringUtils.isBlank(name)) {
            name = "3d";
        }
        if(issueCount == null || issueCount <= 0) {
            issueCount = 1;
        }

        String key = name + issueCount;
        String issueName = name;
        Integer iCount = issueCount;

        SdDrawNoticeResult result = null;
        try {
            result = cacheWrapper.get(key, () -> OkHttpUtils.getSdDrawNoticeResult(issueName, iCount).orElse(null));
            return Optional.ofNullable(result)
                    .map(ret -> new WyfDataResponse<>(ret))
                    .orElse(new WyfErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "查询开奖错误"));
        } catch (Exception e){
            log.error("查询开奖信息报错", e);
            return new WyfErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "查询开奖错误");
        } finally {
            log.info("查询开奖结果 result:{}", Optional.ofNullable(result).map(ret -> ret));
        }
    }

}
