package org.kylin.application;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.kylin.bean.p3.W3D2SumCodeReq;
import org.kylin.bean.p5.WCode;
import org.kylin.constant.ExportPatternEnum;
import org.kylin.service.common.IWCodeEncodeService;
import org.kylin.service.exporter.DocHolder;
import org.kylin.service.exporter.ExportToolSelector;
import org.kylin.service.exporter.IDocExportTool;
import org.kylin.service.xcode.filters.impl.*;
import org.kylin.util.ExporterControlUtil;
import org.kylin.util.TransferUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;

@Service
@Slf4j
public class W3DBinSumFreqApplicationService {

    @Resource
    private IWCodeEncodeService iwCodeEncodeService;

    @Resource
    private ExportToolSelector exportToolSelector;

    public List<WCode> doComposition(List<String> riddles){
        if(CollectionUtils.isEmpty(riddles) || CollectionUtils.size(riddles) <3 ){
            return Collections.emptyList();
        }
        List<Set<Integer>> sets = TransferUtil.toIntegerSets(riddles);
        if(CollectionUtils.isEmpty(sets) || sets.size() > 4 || sets.size() < 3){
            log.info("预测序列不合法 riddles:{}", riddles);
            throw new RuntimeException("预测序列为空");
        }
        List<WCode> wCodes = iwCodeEncodeService.compositionWithQuibinaryEncode(sets);
        log.info("组码结果 sets:{}, wCodes_size:{}", sets, CollectionUtils.size(wCodes));
        return wCodes;
    }

    public List<WCode> doKill(W3D2SumCodeReq req){

        if(Objects.isNull(req)){
            return Collections.emptyList();
        }

        List<WCode> target = req.getWCodes();
        int count = CollectionUtils.size(target);
        log.info("杀码前 {} 注3D", count);

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


        if(CollectionUtils.isNotEmpty(target)
                && StringUtils.isNotBlank(req.getKdSeq())){
            target = new KdSimpleFilter().filter(target, req.getKdSeq());
            log.info("跨度杀 {} 注3D", (count - CollectionUtils.size(target)));
            count = CollectionUtils.size(target);
        }

        if(CollectionUtils.isNotEmpty(target)
                && StringUtils.isNotBlank(req.getBinSumValues())){
            target = new BinSumFilter().filter(target, req.getBinSumValues());
            log.info("二和杀 {} 注3D", (count - CollectionUtils.size(target)));
            count = CollectionUtils.size(target);
        }

        if(CollectionUtils.isNotEmpty(target)
                && StringUtils.isNotBlank(req.getBoldFreqValues())){
            target = new BoldFreqFilter().filter(target, req.getBoldFreqValues());
            log.info("胆频杀 {} 注3D", (count - CollectionUtils.size(target)));
            count = CollectionUtils.size(target);
        }

        log.info("杀码后 {} 注3D", count);
        return target;
    }


    public Optional<String> exportCodeToFile(W3D2SumCodeReq req) throws IOException {

        ExporterControlUtil.setPatternType(ExportPatternEnum.BIN_SUM_FREQ_3D);

        try {
            // 策略导出
            DocHolder docHolder = new DocHolder();
            Optional<IDocExportTool> iDocExportTool = exportToolSelector.getByExportPattern(ExportPatternEnum.BIN_SUM_FREQ_3D);

            if (iDocExportTool.isPresent()) {
                iDocExportTool.get().writeTitleAsDefaultFormat(docHolder, "我要发·" + ExportPatternEnum.BIN_SUM_FREQ_3D.getDesc());
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
