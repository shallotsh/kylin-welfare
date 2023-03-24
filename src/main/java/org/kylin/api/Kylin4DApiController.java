package org.kylin.api;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.kylin.application.W4DApplicationService;
import org.kylin.bean.*;
import org.kylin.bean.p4.W3DCompoundCodeReq;
import org.kylin.bean.p5.WCode;
import org.kylin.bean.p5.WCodeSummarise;
import org.kylin.constant.ExportPatternEnum;
import org.kylin.util.ExporterControlUtil;
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
@RequestMapping("/api/4d")
@Slf4j
public class Kylin4DApiController {

    @Resource
    private W4DApplicationService w4DApplicationService;

    @ResponseBody
    @RequestMapping(value = "/shuffle", method = RequestMethod.POST)
    public WyfResponse shuffle2d(@RequestBody BaseCodeReq req){
        log.info("4d 2sum shuffle req:{}", req);

        if(Objects.isNull(req) || CollectionUtils.isEmpty(req.getSequences())){
            log.warn(" 参数错误");
            return WyfErrorResponse.buildErrorResponse();
        }

        List<WCode> wCodes = w4DApplicationService.doComposition(req.getSequences());
        log.info("4d shuffle ret: {}", wCodes);
        Integer pairCount = WCodeUtils.getPairCodeCount(wCodes);

        WCodeSummarise summarise = new WCodeSummarise();
        summarise.setwCodes(wCodes);
        summarise.setPairCodes(pairCount);
        summarise.setNonPairCodes(CollectionUtils.size(wCodes) - pairCount);

        return new WyfDataResponse<>(summarise);
    }

    @ResponseBody
    @RequestMapping(value = "/kill/code", method = RequestMethod.POST)
    public WyfResponse killCode(@RequestBody W3DCompoundCodeReq req, HttpServletRequest request){
        log.info("4d kill : {}", req);

        if(Objects.isNull(req)){
            log.warn(" 参数错误");
            return WyfErrorResponse.buildErrorResponse();
        }

        List<WCode> allRet = w4DApplicationService.doKill(req);

        boolean freqSeted = allRet.stream().anyMatch(wCode -> wCode.getFreq()>0);
        if(freqSeted){
            Collections.sort(allRet);
        }

        List<WCode> ret = allRet.stream().filter(wCode -> !wCode.isBeDeleted()).collect(Collectors.toList());
        List<WCode> currentDeletedCodes = allRet.stream().filter(wCode -> wCode.isBeDeleted()).collect(Collectors.toList());
        if(CollectionUtils.isNotEmpty(req.getDeletedCodes())) {
            currentDeletedCodes.addAll(req.getDeletedCodes().stream().flatMap(x -> x.getData().stream()).collect(Collectors.toList()));
        }

        List<LabelValue<List<WCode>>> deletedCodes = Arrays.asList(new LabelValue<>("DeletedBy4D", currentDeletedCodes));
        log.info("4d kill code ret: {}", ret);

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
    @RequestMapping(value = "/codes/transferCode",  method = RequestMethod.POST)
    public WyfResponse transferCode(@RequestBody W3DCompoundCodeReq req) {

        log.info("transfer and export req:{}", req);
        if(req == null){
            return new WyfErrorResponse(HttpStatus.BAD_REQUEST.value(), "转换错误");
        }

        List<WCode> ret = w4DApplicationService.transferToThreeCode(req.getWCodes());
        log.info("四转三码 ret_size:{}", CollectionUtils.size(ret));
        Integer pairCount = WCodeUtils.getPairCodeCount(ret);
        WCodeSummarise summarise = new WCodeSummarise();
        summarise.setwCodes(ret);
        summarise.setFreqSeted(false);
        summarise.setPairCodes(pairCount);
        summarise.setNonPairCodes(CollectionUtils.size(ret) - pairCount);

        return  new WyfDataResponse<>(summarise);
    }

    @ResponseBody
    @RequestMapping(value = "/codes/export",  method = RequestMethod.POST)
    public WyfResponse exportCodes(@RequestBody W3DCompoundCodeReq req) {
        log.info("4d export req:{}", req);
        if(req == null){
            return new WyfErrorResponse(HttpStatus.BAD_REQUEST.value(), "导出数据错误");
        }
        try {
            // 导出上下文设置
            if(Objects.equals(Boolean.TRUE, req.getFourToThreeCmd())){
                ExporterControlUtil.setPatternType(ExportPatternEnum.WCODE_4D_TO_3D);
            }else {
                ExporterControlUtil.setPatternType(ExportPatternEnum.WCODE_4D);
            }
            Optional<String> optFile = w4DApplicationService.exportCodeToFile(req);
            return optFile.map(f -> new WyfDataResponse(f)).orElse(new WyfErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "服务器内部错误"));
        } catch (IOException e) {
            log.error("导出文件错误", e);
            return new WyfErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "服务器内部错误");
        }
    }


}
