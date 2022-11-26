package org.kylin.api;

import lombok.extern.slf4j.Slf4j;
import org.kylin.bean.WyfDataResponse;
import org.kylin.bean.WyfErrorResponse;
import org.kylin.bean.WyfResponse;
import org.kylin.bean.p3.ExpertCodeReq;
import org.kylin.service.p3.ExpertCodeService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Optional;

@Controller
@RequestMapping("/api/3d-2sum")
@Slf4j
public class Kylin3DBinSumApiController {

    @Resource
    private ExpertCodeService expertCodeService;


    @ResponseBody
    @RequestMapping(value = "/shuffle", method = RequestMethod.POST)
    public WyfResponse shuffle2d(@RequestBody ExpertCodeReq req){

        return null;
    }

    @ResponseBody
    @RequestMapping(value = "/kill/code", method = RequestMethod.POST)
    public WyfResponse killCode(@RequestBody ExpertCodeReq req, HttpServletRequest request){

        return null;
    }


    @ResponseBody
    @RequestMapping(value = "/codes/export",  method = RequestMethod.POST)
    public WyfResponse exportCodes(@RequestBody ExpertCodeReq req) {
        if(req == null){
            return new WyfErrorResponse(HttpStatus.BAD_REQUEST.value(), "导出数据错误");
        }
        log.info("export 3d req:{}", req);
        try {
            Optional<String> optFile = expertCodeService.exportCodeToFile(req);
            return optFile.map(f -> new WyfDataResponse(f)).orElse(new WyfErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "服务器内部错误"));
        } catch (IOException e) {
            log.error("导出文件错误", e);
            return new WyfErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "服务器内部错误");
        }
    }


}
