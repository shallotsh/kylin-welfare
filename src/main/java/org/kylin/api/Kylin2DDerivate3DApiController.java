package org.kylin.api;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.kylin.bean.WyfDataResponse;
import org.kylin.bean.WyfErrorResponse;
import org.kylin.bean.WyfResponse;
import org.kylin.bean.p3.TwoDeriveThreeReq;
import org.kylin.bean.p5.WCode;
import org.kylin.bean.p5.WCodeSummarise;
import org.kylin.service.p3.TwoDeriveThreeCodeService;
import org.kylin.util.TransferUtil;
import org.kylin.util.WCodeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api/two-derive-three")
@Slf4j
public class Kylin2DDerivate3DApiController {

    @Autowired
    private TwoDeriveThreeCodeService twoDeriveThreeCodeService;



    @ResponseBody
    @RequestMapping(value = "/shuffle", method = RequestMethod.POST)
    public WyfResponse shuffleCodes(@RequestBody TwoDeriveThreeReq req){

        log.info("two-derive-three shuffle req:{}", req);

        if(Objects.isNull(req) || CollectionUtils.isEmpty(req.getSequences())){
            log.warn(" 参数错误");
            return WyfErrorResponse.buildErrorResponse();
        }
        List<List<Integer>> riddlesList = TransferUtil.toMultiList(req.getSequences().get(0));
        List<WCode> ret = twoDeriveThreeCodeService.shuffleCodes(riddlesList);

        log.info("two-derive-three shuffle ret: {}", ret);

        Integer pairCount = WCodeUtils.getPairCodeCount(ret);
        WCodeSummarise summarise = new WCodeSummarise();
        summarise.setwCodes(ret);
        summarise.setPairCodes(pairCount);
        summarise.setNonPairCodes(CollectionUtils.size(ret) - pairCount);

        return new WyfDataResponse<>(summarise);
    }

    @ResponseBody
    @RequestMapping(value = "/convert/direct", method = RequestMethod.POST)
    public WyfResponse convertToDirectCodes(@RequestBody TwoDeriveThreeReq req, HttpServletRequest request){
        log.info("转直选 req={}", JSON.toJSONString(req));

        List<WCode> groupCodes = twoDeriveThreeCodeService.convertToDirectCodes(req.getWCodes());
        Integer pairCount = WCodeUtils.getPairCodeCount(groupCodes);

        WCodeSummarise summarise = new WCodeSummarise();
        summarise.setwCodes(groupCodes);
        summarise.setPairCodes(pairCount);
        summarise.setNonPairCodes(CollectionUtils.size(groupCodes) - pairCount);

        return  new WyfDataResponse<>(summarise);
    }



    @ResponseBody
    @RequestMapping(value = "/kill/code", method = RequestMethod.POST)
    public WyfResponse killCode(@RequestBody TwoDeriveThreeReq req, HttpServletRequest request){

        log.info("2d derive 3d kill req:{}", JSON.toJSONString(req));

        if(Objects.isNull(req)){
            log.warn(" 参数错误");
            return WyfErrorResponse.buildErrorResponse();
        }

        List<WCode> allRet = twoDeriveThreeCodeService.killCode(req);
        List<WCode> ret = allRet.stream().filter(wCode -> !wCode.isBeDeleted()).collect(Collectors.toList());
        log.info("2d derive 3d kill code ret: {}", ret);

        Integer pairCount = WCodeUtils.getPairCodeCount(ret);
        WCodeSummarise summarise = new WCodeSummarise();
        summarise.setwCodes(ret);
        summarise.setPairCodes(pairCount);
        summarise.setNonPairCodes(CollectionUtils.size(ret) - pairCount);

       return  new WyfDataResponse<>(summarise);
    }


    @ResponseBody
    @RequestMapping(value = "/codes/export",  method = RequestMethod.POST)
    public WyfResponse exportCodes(@RequestBody TwoDeriveThreeReq req) {
        if(req == null){
            return new WyfErrorResponse(HttpStatus.BAD_REQUEST.value(), "导出数据错误");
        }
        log.info("export req:{}", req);

        Optional<String> optFile = null;
        try {
            optFile = twoDeriveThreeCodeService.exportCodeToFile(req);
            return optFile.map(f -> new WyfDataResponse(f)).orElse(new WyfErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "服务器内部错误"));
        } catch (IOException e) {
            log.error("导出文件错误", e);
            return new WyfErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "服务器内部错误");
        }
    }


}
