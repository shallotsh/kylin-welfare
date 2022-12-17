package org.kylin.api;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.kylin.application.WTailSumApplicationService;
import org.kylin.bean.*;
import org.kylin.util.TransferUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.*;

@Controller
@RequestMapping("/api/tail-sum")
@Slf4j
public class KylinTailSumApiController {

    @Resource
    private WTailSumApplicationService applicationService;

    @ResponseBody
    @RequestMapping(value = "/shuffle", method = RequestMethod.POST)
    public WyfResponse tailSumByComposition(@RequestBody BaseCodeReq req){
        log.info("tail sum shuffle req:{}", req);

        if(Objects.isNull(req) || CollectionUtils.isEmpty(req.getSequences())){
            log.warn(" 参数错误");
            return WyfErrorResponse.buildErrorResponse();
        }

        List<Integer> riddleSet = TransferUtil.toIntegerList(req.getSequences().get(0));
        TailSumValue tailSumValue = applicationService.computeTailSumValueByComposition(riddleSet);
        log.info("tail sum ret: {}", tailSumValue);

        return new WyfDataResponse<>(tailSumValue);
    }
}
