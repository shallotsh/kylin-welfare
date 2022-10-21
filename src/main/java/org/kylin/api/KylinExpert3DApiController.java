package org.kylin.api;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.kylin.bean.LabelValue;
import org.kylin.bean.WyfDataResponse;
import org.kylin.bean.WyfErrorResponse;
import org.kylin.bean.WyfResponse;
import org.kylin.bean.p3.ExpertCodeReq;
import org.kylin.bean.p5.WCode;
import org.kylin.bean.p5.WCodeSummarise;
import org.kylin.service.p3.ExpertCodeService;
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
@RequestMapping("/api/expert/3d")
@Slf4j
public class KylinExpert3DApiController {

    @Autowired
    private ExpertCodeService expertCodeService;



    @ResponseBody
    @RequestMapping(value = "/shuffle", method = RequestMethod.POST)
    public WyfResponse shuffle2d(@RequestBody ExpertCodeReq req){

        log.info("expert shuffle req:{}", req);

        if(Objects.isNull(req) || CollectionUtils.isEmpty(req.getSequences())){
            log.warn(" 参数错误");
            return WyfErrorResponse.buildErrorResponse();
        }

        List<Integer> riddles = TransferUtil.toIntegerList(req.getSequences().get(0));

        List<WCode> ret = expertCodeService.expertEncode(riddles);

        log.info("expert shuffle ret: {}", ret);
        Integer pairCount = WCodeUtils.getPairCodeCount(ret);

        WCodeSummarise summarise = new WCodeSummarise();
        summarise.setwCodes(ret);
        summarise.setPairCodes(pairCount);
        summarise.setNonPairCodes(CollectionUtils.size(ret) - pairCount);

        return new WyfDataResponse<>(summarise);
    }

    @ResponseBody
    @RequestMapping(value = "/convert/2d", method = RequestMethod.POST)
    public WyfResponse convertTo2DCodes(@RequestBody ExpertCodeReq req, HttpServletRequest request){
        log.info("转2D req={}", JSON.toJSONString(req));
        List<WCode> groupCodes = expertCodeService.convertTo2DCodesForEveryFreq(req.getWCodes());
        Integer pairCount = WCodeUtils.getPairCodeCount(groupCodes);

        WCodeSummarise summarise = new WCodeSummarise();
        summarise.setwCodes(groupCodes);
        summarise.setFreqSeted(req.getFreqSeted() == null ? false: req.getFreqSeted());

        List<LabelValue<List<WCode>>> deletedCodes = req.getDeletedCodes();
        if(CollectionUtils.isNotEmpty(deletedCodes)) {
            LabelValue<List<WCode>> labelValue = deletedCodes.get(0);
            // 已删除编码也转组选
            List<WCode> deleteCodes = expertCodeService.convertTo2DCodesForEveryFreq(labelValue.getData());
            summarise.setDeletedCodes(Arrays.asList(new LabelValue<>(labelValue.getLabel(), deleteCodes)));
        }
        summarise.setPairCodes(pairCount);
        summarise.setNonPairCodes(CollectionUtils.size(groupCodes) - pairCount);

        return  new WyfDataResponse<>(summarise);
    }

    @ResponseBody
    @RequestMapping(value = "/convert/group", method = RequestMethod.POST)
    public WyfResponse convertGroupCodes(@RequestBody ExpertCodeReq req, HttpServletRequest request){
        log.info("转组选 req={}", JSON.toJSONString(req));

        List<WCode> groupCodes = expertCodeService.convertToGroupCodesForEveryFreq(req.getWCodes());
        Integer pairCount = WCodeUtils.getPairCodeCount(groupCodes);

        WCodeSummarise summarise = new WCodeSummarise();
        summarise.setwCodes(groupCodes);
        summarise.setFreqSeted(req.getFreqSeted() == null ? false: req.getFreqSeted());

        List<LabelValue<List<WCode>>> deletedCodes = req.getDeletedCodes();
        if(CollectionUtils.isNotEmpty(deletedCodes)) {
            LabelValue<List<WCode>> labelValue = deletedCodes.get(0);
            // 已删除编码也转组选
            List<WCode> deleteCodes = expertCodeService.convertToGroupCodesForEveryFreq(labelValue.getData());
            summarise.setDeletedCodes(Arrays.asList(new LabelValue<>(labelValue.getLabel(), deleteCodes)));
        }
        summarise.setPairCodes(pairCount);
        summarise.setNonPairCodes(CollectionUtils.size(groupCodes) - pairCount);

        return  new WyfDataResponse<>(summarise);
    }



    @ResponseBody
    @RequestMapping(value = "/kill/code", method = RequestMethod.POST)
    public WyfResponse killCode(@RequestBody ExpertCodeReq req, HttpServletRequest request){

        log.info("expert kill req:{}", req);

        if(Objects.isNull(req)){
            log.warn(" 参数错误");
            return WyfErrorResponse.buildErrorResponse();
        }

        List<WCode> allRet = expertCodeService.killCode(req);
        boolean freqSeted = allRet.stream().anyMatch(wCode -> wCode.getFreq()>0);
        if(freqSeted){
            Collections.sort(allRet);
        }

        List<WCode> ret = allRet.stream().filter(wCode -> !wCode.isBeDeleted()).collect(Collectors.toList());
        List<LabelValue<List<WCode>>> deletedCodes = Arrays.asList(new LabelValue<>("", allRet.stream().filter(wCode -> wCode.isBeDeleted()).collect(Collectors.toList())));
        log.info("expert kill code ret: {}", ret);

        Integer pairCount = WCodeUtils.getPairCodeCount(ret);
        WCodeSummarise summarise = new WCodeSummarise();
        summarise.setwCodes(ret);
        summarise.setFreqSeted(freqSeted);
        summarise.setDeletedCodes(deletedCodes);
        summarise.setPairCodes(pairCount);
        summarise.setNonPairCodes(CollectionUtils.size(ret) - pairCount);

       return  new WyfDataResponse<>(summarise);
    }

//    @ResponseBody
//    @RequestMapping(value = "/comp/select", method = RequestMethod.POST)
//    public WyfResponse compSelect(@RequestBody XCodeReq req){
//        log.info("comp-select req:{}", req);
//
//        return  new WyfDataResponse<>(new WCodeSummarise().setwCodes(xCodeService.compSelectCodes(req)).setFreqSeted(true));
//    }


    @ResponseBody
    @RequestMapping(value = "/codes/export",  method = RequestMethod.POST)
    public WyfResponse exportCodes(@RequestBody ExpertCodeReq req) {
        if(req == null){
            return new WyfErrorResponse(HttpStatus.BAD_REQUEST.value(), "导出数据错误");
        }

        Optional<String> optFile = null;
        try {
            optFile = expertCodeService.exportCodeToFile(req);
            return optFile.map(f -> new WyfDataResponse(f)).orElse(new WyfErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "服务器内部错误"));
        } catch (IOException e) {
            log.error("导出文件错误", e);
            return new WyfErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "服务器内部错误");
        }
    }


}
