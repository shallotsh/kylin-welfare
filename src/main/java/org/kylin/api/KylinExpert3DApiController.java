package org.kylin.api;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.kylin.bean.WyfDataResponse;
import org.kylin.bean.WyfErrorResponse;
import org.kylin.bean.WyfResponse;
import org.kylin.bean.p2.XCodeReq;
import org.kylin.bean.p3.ExpertCodeReq;
import org.kylin.bean.p5.WCode;
import org.kylin.bean.p5.WCodeSummarise;
import org.kylin.service.p3.ExpertCodeService;
import org.kylin.service.xcode.XCodeService;
import org.kylin.util.TransferUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Controller
@RequestMapping("/api/expert/3d")
@Slf4j
public class KylinExpert3DApiController {

    @Autowired
    private XCodeService xCodeService;

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

        return new WyfDataResponse<>(new WCodeSummarise().setwCodes(ret));
    }


    @ResponseBody
    @RequestMapping(value = "/kill/code", method = RequestMethod.POST)
    public WyfResponse killCode(@RequestBody ExpertCodeReq req, HttpServletRequest request){

        log.info("expert kill req:{}", req);

        if(Objects.isNull(req)){
            log.warn(" 参数错误");
            return WyfErrorResponse.buildErrorResponse();
        }

        List<WCode> ret = expertCodeService.killCode(req);

        log.info("expert kill code ret: {}", ret);

       return  new WyfDataResponse<>(new WCodeSummarise().setwCodes(ret));
    }

    @ResponseBody
    @RequestMapping(value = "/comp/select", method = RequestMethod.POST)
    public WyfResponse compSelect(@RequestBody XCodeReq req){
        log.info("comp-select req:{}", req);

        return  new WyfDataResponse<>(new WCodeSummarise().setwCodes(xCodeService.compSelectCodes(req)).setFreqSeted(true));
    }


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
