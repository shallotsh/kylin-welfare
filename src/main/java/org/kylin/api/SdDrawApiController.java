package org.kylin.api;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.kylin.bean.WyfDataResponse;
import org.kylin.bean.WyfErrorResponse;
import org.kylin.bean.WyfResponse;
import org.kylin.bean.sd.SdDrawNoticeResult;
import org.kylin.constant.ESIndexEnum;
import org.kylin.util.MyDateUtil;
import org.kylin.util.OkHttpUtils;
import org.kylin.wrapper.ESWrapper;
import org.kylin.wrapper.GuavaCacheWrapper;
import org.kylin.wrapper.bo.SdDrawResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;

@RestController
@RequestMapping("/api/3d")
@Slf4j
public class SdDrawApiController {



    @Autowired
    private GuavaCacheWrapper<SdDrawNoticeResult> cacheWrapper;

    @Resource
    private ESWrapper esWrapper;

    @RequestMapping(value = "/draw/notice", method = RequestMethod.GET)
    public WyfResponse findDrawNotice(String name, Integer issueCount){

        if(StringUtils.isBlank(name)) {
            name = "3d";
        }
        if(issueCount == null || issueCount <= 0) {
            issueCount = 1;
        }

        LocalDate drawDate = MyDateUtil.getLatestDrawDate();
        String key = "3D_"+drawDate;

        SdDrawNoticeResult result = null;
        try {
            result = cacheWrapper.get(key, () -> OkHttpUtils.getSdDrawNoticeResult(drawDate, drawDate).orElse(null));

            // 记录结果
            SdDrawResult res = SdDrawResult.from(result.getResult().get(0));
            if(!esWrapper.exists(ESIndexEnum.WELFARE_RESULT.getIndex(), res.getCode())) {
                esWrapper.index(ESIndexEnum.WELFARE_RESULT.getIndex(), res.getCode(), res);
            }

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
