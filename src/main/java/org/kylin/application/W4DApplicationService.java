package org.kylin.application;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.kylin.bean.p3.W3D2SumCodeReq;
import org.kylin.bean.p4.W3DCompoundCodeReq;
import org.kylin.bean.p5.WCode;
import org.kylin.constant.ExportPatternEnum;
import org.kylin.service.common.IWCodeEncodeService;
import org.kylin.service.exporter.DocHolder;
import org.kylin.service.exporter.ExportToolSelector;
import org.kylin.service.exporter.IDocExportTool;
import org.kylin.service.xcode.filters.impl.BoldCodeFilter;
import org.kylin.service.xcode.filters.impl.FishManCodeFilter;
import org.kylin.service.xcode.filters.impl.LateAutumnCodeFilter;
import org.kylin.service.xcode.filters.impl.SumTailCodeFilter;
import org.kylin.util.ExporterControlUtil;
import org.kylin.util.TransferUtil;
import org.kylin.util.WCodeUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class W4DApplicationService {

    @Resource
    private IWCodeEncodeService iwCodeEncodeService;

    @Resource
    private ExportToolSelector exportToolSelector;

    public List<WCode> doComposition(List<String> riddles){
        if(CollectionUtils.isEmpty(riddles) || CollectionUtils.size(riddles) != 1 ){
            return Collections.emptyList();
        }
        List<Integer> seq = TransferUtil.toIntegerList(riddles.get(0));
        if(CollectionUtils.isEmpty(seq)){
            log.info("预测序列不合法 riddles:{}", riddles);
            throw new RuntimeException("预测序列为空");
        }
        List<WCode> wCodes = iwCodeEncodeService.combine4Code(seq, 4);
        // abcd中有一对重复的也留下，即aabc型留下，其余aabb型（a不等于b），aaab，aaaa型都去除
        wCodes = wCodes.stream().filter(x -> WCodeUtils.meetConditionFor4D(x)).collect(Collectors.toList());

        log.info("组码结果 seq:{}, wCodes_size:{}", seq, CollectionUtils.size(wCodes));
        return wCodes;
    }

    public List<WCode> doKill(W3DCompoundCodeReq req){

        if(Objects.isNull(req)){
            return Collections.emptyList();
        }

        List<WCode> target = req.getWCodes();
        int count = CollectionUtils.size(target);
        log.info("4d杀码前 {} 注3D", count);

        if(CollectionUtils.isNotEmpty(target) && StringUtils.isNotBlank(req.getBoldCodeSeq())){
            target  = new BoldCodeFilter().filter(target, req.getBoldCodeSeq());
            log.info("胆杀 {} 注3D", (count - CollectionUtils.size(target)));
            count = CollectionUtils.size(target);
        }


        if(CollectionUtils.isNotEmpty(target) && StringUtils.isNotBlank(req.getSumTailValues())){
            target = new SumTailCodeFilter().filter(target, req.getSumTailValues());
            log.info("三和尾杀 {} 住3D", (count - CollectionUtils.size(target)));
            count = CollectionUtils.size(target);
        }


        if(CollectionUtils.isNotEmpty(target) && StringUtils.isNotBlank(req.getLateAutumnCode())){
            target = new LateAutumnCodeFilter().filter(target, req.getLateAutumnCode());
            log.info("晚秋选码 {} 住3D", (count - CollectionUtils.size(target)));
            count = CollectionUtils.size(target);
        }

//        if(CollectionUtils.isNotEmpty(target)
//                && StringUtils.isNotBlank(req.getKdSeq())){
//            target = new KdSimpleFilter().filter(target, req.getKdSeq());
//            log.info("跨度杀 {} 注3D", (count - CollectionUtils.size(target)));
//            count = CollectionUtils.size(target);
//        }

//        if(CollectionUtils.isNotEmpty(target)
//                && StringUtils.isNotBlank(req.getBinSumValues())){
//            target = new BinSumFilter().filter(target, req.getBinSumValues());
//            log.info("二和杀 {} 注3D", (count - CollectionUtils.size(target)));
//            count = CollectionUtils.size(target);
//        }

        log.info("4d杀码后 {} 注3D", count);
        return target;
    }


//    public List<WCode> transferFourToThree(W3DCompoundCodeReq req){
//        if(req == null || CollectionUtils.isEmpty(req.getWCodes())){
//            log.info("入参数据为空");
//            return Collections.emptyList();
//        }
//        List<WCode> wCodes = req.getwCodes();
//
//
//
//
//
//        return Collections.emptyList();
//    }
//
//
//    private List<WCode> getDecomposeCodes
//
//






    public Optional<String> exportCodeToFile(W3DCompoundCodeReq req) throws IOException {

        try {
            ExportPatternEnum exportPatternEnum = ExporterControlUtil.getPatternType();
            if(exportPatternEnum == null){
                log.info("导出模式获取为空，导出失败 req:{}", JSON.toJSONString(req));
                return Optional.empty();
            }
            // 策略导出
            DocHolder docHolder = new DocHolder();
            Optional<IDocExportTool> iDocExportTool = exportToolSelector.getByExportPattern(exportPatternEnum);

            if (iDocExportTool.isPresent()) {
                iDocExportTool.get().writeTitleAsDefaultFormat(docHolder, "我要发·" + exportPatternEnum.getDesc());
                iDocExportTool.get().writeContentToDoc(docHolder, req.adaptToWCodeReq());
                String fileName = iDocExportTool.get().exportDocAsFile(docHolder);
                return Optional.of(fileName);
            } else {
                return Optional.empty();
            }
        } finally {
            ExporterControlUtil.clearPatternType();
        }
    }
}
