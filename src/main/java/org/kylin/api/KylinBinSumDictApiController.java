package org.kylin.api;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.kylin.application.W3DBinSumCommonApplicationService;
import org.kylin.bean.*;
import org.kylin.bean.p3.W3D2SumCodeReq;
import org.kylin.bean.p5.WCode;
import org.kylin.bean.p5.WCodeSummarise;
import org.kylin.constant.ExportPatternEnum;
import org.kylin.util.WCodeUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api/2sum-dict")
@Slf4j
public class KylinBinSumDictApiController {

    @Resource
    private W3DBinSumCommonApplicationService w3DBinSumCommonApplicationService;

    @ResponseBody
    @RequestMapping(value = "/shuffle", method = RequestMethod.POST)
    public WyfResponse shuffle3d(@RequestBody BaseCodeReq req){
        log.info("2sum dict shuffle req:{}", req);

        if(Objects.isNull(req) || CollectionUtils.isEmpty(req.getSequences())){
            log.warn(" 参数错误");
            return WyfErrorResponse.buildErrorResponse();
        }

        List<WCode> wCodes = w3DBinSumCommonApplicationService.doCompositionByDict(req.getSequences());
        log.info("2sum dict shuffle ret: {}", wCodes);
        Integer pairCount = WCodeUtils.getPairCodeCount(wCodes);

        WCodeSummarise summarise = new WCodeSummarise();
        summarise.setwCodes(wCodes);
        summarise.setPairCodes(pairCount);
        summarise.setNonPairCodes(CollectionUtils.size(wCodes) - pairCount);

        return new WyfDataResponse<>(summarise);
    }

    @ResponseBody
    @RequestMapping(value = "/kill/code", method = RequestMethod.POST)
    public WyfResponse killCode(@RequestBody W3D2SumCodeReq req, HttpServletRequest request){
        log.info("2sumDict杀码 : {}", req);

        if(Objects.isNull(req)){
            log.warn(" 参数错误");
            return WyfErrorResponse.buildErrorResponse();
        }

        List<WCode> allRet =  w3DBinSumCommonApplicationService.doKill(req);

        boolean freqSeted = allRet.stream().anyMatch(wCode -> wCode.getFreq()>0);
        if(freqSeted){
            Collections.sort(allRet);
        }

        List<WCode> ret = allRet.stream().filter(wCode -> !wCode.isBeDeleted()).collect(Collectors.toList());
        List<LabelValue<List<WCode>>> deletedCodes = Arrays.asList(new LabelValue<>("", allRet.stream().filter(wCode -> wCode.isBeDeleted()).collect(Collectors.toList())));
        log.info("w3d 2sum kill code ret: {}", ret);

        Integer pairCount = WCodeUtils.getPairCodeCount(ret);
        WCodeSummarise summarise = new WCodeSummarise();
        summarise.setwCodes(ret);
        summarise.setFreqSeted(freqSeted);
        summarise.setDeletedCodes(deletedCodes);
        summarise.setPairCodes(pairCount);
        summarise.setNonPairCodes(CollectionUtils.size(ret) - pairCount);

        return  new WyfDataResponse<>(summarise);
    }


    @ResponseBody
    @RequestMapping(value = "/codes/export",  method = RequestMethod.POST)
    public WyfResponse exportCodes(@RequestBody W3D2SumCodeReq req) {
        log.info("2sumDict导出:{}", req);
        if(req == null){
            return new WyfErrorResponse(HttpStatus.BAD_REQUEST.value(), "导出数据错误");
        }
        try {
            Optional<String> optFile = w3DBinSumCommonApplicationService.exportCodeToFile(req, ExportPatternEnum.BIN_SUM_DICT_3D);
            log.info("2sumDict导出文件:{}", optFile.map(String::toString).orElse("未知"));
            return optFile.map(f -> new WyfDataResponse(f)).orElse(new WyfErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "服务器内部错误"));
        } catch (IOException e) {
            log.error("导出文件错误", e);
            return new WyfErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "服务器内部错误");
        }
    }


}
