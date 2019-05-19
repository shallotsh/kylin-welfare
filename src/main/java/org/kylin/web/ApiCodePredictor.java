package org.kylin.web;

import com.alibaba.fastjson.JSON;
import org.apache.commons.collections4.CollectionUtils;
import org.kylin.bean.*;
import org.kylin.constant.CodeTypeEnum;
import org.kylin.service.WelfareCodePredictor;
import org.kylin.util.CommonUtils;
import org.kylin.util.DocUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * @author huangyawu
 * @date 2017/6/25 下午3:30.
 */
@Controller
@RequestMapping("/api/welfare")
public class ApiCodePredictor {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiCodePredictor.class);

    @Resource
    private WelfareCodePredictor welfareCodePredictor;

    @ResponseBody
    @RequestMapping(value = "/codes/predict",  method = {RequestMethod.POST, RequestMethod.GET})
    public WyfResponse predict(@RequestBody WyfParam wyfParam){
        LOGGER.info("api-code-predictor-predict param={}", JSON.toJSONString(wyfParam));

        if(wyfParam == null || CollectionUtils.isEmpty(wyfParam.getRiddles())
                || wyfParam.getTargetCodeType() == null){
            LOGGER.warn("api-code-predictor-predict-bad-quest");
            return new WyfErrorResponse(HttpStatus.BAD_REQUEST.value(), "param invalid.");
        }

        WelfareCode welfareCode = welfareCodePredictor.encode(wyfParam.getRiddles(), CodeTypeEnum.getById(wyfParam.getTargetCodeType()));

        LOGGER.debug("codes:{}", JSON.toJSONString(welfareCode));

        return new WyfDataResponse<>(welfareCode);
    }

    @ResponseBody
    @RequestMapping(value = "/codes/transfer",  method = RequestMethod.POST)
    public WyfResponse transfer(@RequestBody WelfareCode welfareCode){
        LOGGER.info("transfer-codes welfareCode={}", JSON.toJSONString(welfareCode));
        if(welfareCode == null || welfareCode.getW3DCodes() == null){
            LOGGER.warn("参数不合法");
            return new WyfErrorResponse(HttpStatus.BAD_REQUEST.value(), "参数为空");
        }

        if(CodeTypeEnum.DIRECT == welfareCode.getCodeTypeEnum()){
            return new WyfDataResponse<>(welfareCode.toGroup().sort(WelfareCode::bitSort).generate());
        }else if (CodeTypeEnum.GROUP == welfareCode.getCodeTypeEnum()){
            return new WyfDataResponse<>(welfareCode.toDirect().sort(WelfareCode::bitSort).generate());
        }else{
            return new WyfErrorResponse(HttpStatus.BAD_REQUEST.value(), "编码类型错误");
        }
    }

    @ResponseBody
    @RequestMapping(value = "/codes/filter", method = RequestMethod.POST)
    public WyfResponse doFilter(@RequestBody FilterParam filterParam){

        LOGGER.info("api-code-predictor-filter param={}", filterParam);

        if(filterParam == null || filterParam.getWelfareCode() == null){
            LOGGER.warn("api-code-predictor-filter-bad-quest");
            return new WyfErrorResponse(HttpStatus.BAD_REQUEST.value(), "param invalid.");
        }

        WelfareCode welfareCode = welfareCodePredictor.filter(filterParam);

        if(welfareCode == null){
            return new WyfErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "杀码错误");
        }

        return new WyfDataResponse<>(welfareCode);
    }

    @ResponseBody
    @RequestMapping(value = "/codes/high/freq",  method = RequestMethod.POST)
    public WyfResponse highFreqFilter(@RequestBody WelfareCode welfareCode){
        LOGGER.info("api-code-predictor-high-freq code={}", JSON.toJSONString(welfareCode));
        WelfareCode code = welfareCodePredictor.highFreq(welfareCode);

        return new WyfDataResponse<>(code);
    }

    @ResponseBody
    @RequestMapping(value = "/codes/sum/tail/freq",  method = RequestMethod.POST)
    public WyfResponse increaseFreqBySumTail(@RequestBody P3Param p3Param){
        LOGGER.info("P3和增频 p3Param={}", p3Param);
        WelfareCode code = welfareCodePredictor.increaseFreqBySumTail(p3Param);

        return new WyfDataResponse<>(code);
    }

    @ResponseBody
    @RequestMapping(value = "/codes/bold/freq",  method = RequestMethod.POST)
    public WyfResponse increaseFreqByBoldCode(@RequestBody P3Param p3Param){
        LOGGER.info("P3胆增频 p3Param={}", p3Param);
        WelfareCode code = welfareCodePredictor.increaseFreqByBoldCode(p3Param);

        return new WyfDataResponse<>(code);
    }

    @ResponseBody
    @RequestMapping(value = "/codes/bit/seq/filter",  method = RequestMethod.POST)
    public WyfResponse bitsFilter(@RequestBody P3Param p3Param){
        LOGGER.info("P3位序筛选 p3Param={}", p3Param);
        WelfareCode code = welfareCodePredictor.bitsFilter(p3Param);

        return new WyfDataResponse<>(code);
    }


    @ResponseBody
    @RequestMapping(value = "/codes/minus",  method = RequestMethod.POST)
    public WyfResponse minus(@RequestBody PolyParam polyParam){

        LOGGER.info("api-code-predictor-minus param={}", JSON.toJSONString(polyParam));

        if(polyParam == null || polyParam.getMinuend() == null || polyParam.getSubtractor() == null){
            LOGGER.warn("api-code-predictor-minus-bad-quest");
            return new WyfErrorResponse(HttpStatus.BAD_REQUEST.value(), "param invalid.");
        }

        try {
            WelfareCode ret = welfareCodePredictor.minus(polyParam);

            LOGGER.info("minus-set code={}", ret.getW3DCodes());

            return new WyfDataResponse<>(ret);
        } catch (Exception e) {
            return new WyfErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "取余失败");
        }
    }

    @ResponseBody
    @RequestMapping(value = "/codes/select",  method = RequestMethod.POST)
    public WyfResponse select(@RequestBody PolyParam polyParam){

        LOGGER.info("api-code-predictor-comp-select param:{}", JSON.toJSONString(polyParam));

        if(polyParam == null || CollectionUtils.isEmpty(polyParam.getWelfareCodes())){
            LOGGER.warn("api-code-predictor-comp-select-bad-quest");
            return new WyfErrorResponse(HttpStatus.BAD_REQUEST.value(), "param invalid.");
        }

        try {
            WelfareCode welfareCode = welfareCodePredictor.compSelect(polyParam.getWelfareCodes());

            LOGGER.info("comp-select code={}", welfareCode.getW3DCodes());

            return new WyfDataResponse<>(welfareCode);
        } catch (Exception e) {
            return new WyfErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "综合选码失败");
        }
    }

    @ResponseBody
    @RequestMapping(value = "/codes/export",  method = RequestMethod.POST)
    public WyfResponse exportCodes(@RequestBody String codeString,
                              HttpServletResponse response) throws IOException{
        WelfareCode welfareCode = JSON.parseObject(codeString, WelfareCode.class);
        if(welfareCode == null){
            return new WyfErrorResponse(HttpStatus.BAD_REQUEST.value(), "参数错误");
        }

//        String filename = "welfare_" +  CommonUtils.getCurrentTimeString() + ".docx;";
//        response.setContentType("application/vnd.ms-works");
//        response.setHeader("Content-Disposition", "attachment;fileName=" + filename);
//        response.setHeader("Content-Transfer-Encoding", "binary");
//        DocUtils.saveW3DCodes(welfareCode, response.getOutputStream());


        try {
            String fileName = DocUtils.saveW3DCodes(welfareCode);
            return  new WyfDataResponse<>(fileName);
        } catch (IOException e) {
            LOGGER.error("export-codes-error welfareCode={}", JSON.toJSONString(welfareCode));
            return new WyfErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "服务器内部错误");
        }
    }


    /**
     * 文件下载
     * @Description:
     * @param fileName
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/download")
    public String downloadFile(@RequestParam("fileName") String fileName,
                               HttpServletRequest request, HttpServletResponse response) {
        if (fileName != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("/var/attachment/");

            sb.append(CommonUtils.getCurrentTimeString().substring(0,6));
            sb.append(File.separator);

            File file = new File(sb.toString(), fileName);

            if (file.exists()) {
                response.setContentType("application/force-download");// 设置强制下载不打开
                response.addHeader("Content-Disposition",
                        "attachment;fileName=" + fileName);// 设置文件名
                byte[] buffer = new byte[1024];
                FileInputStream fis = null;
                BufferedInputStream bis = null;
                try {
                    fis = new FileInputStream(file);
                    bis = new BufferedInputStream(fis);
                    OutputStream os = response.getOutputStream();
                    int i = bis.read(buffer);
                    while (i != -1) {
                        os.write(buffer, 0, i);
                        i = bis.read(buffer);
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
                } finally {
                    if (bis != null) {
                        try {
                            bis.close();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    if (fis != null) {
                        try {
                            fis.close();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return null;
    }
}
