package org.kylin.service.pfive.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.kylin.algorithm.strategy.SequenceProcessor;
import org.kylin.algorithm.strategy.Strategy;
import org.kylin.bean.p5.WCode;
import org.kylin.bean.p5.WCodeReq;
import org.kylin.bean.p5.WCodeSummarise;
import org.kylin.constant.ExportPatternEnum;
import org.kylin.constant.FilterStrategyEnum;
import org.kylin.factory.StrategyFactory;
import org.kylin.service.exporter.AbstractDocumentExporter;
import org.kylin.service.exporter.DocHolder;
import org.kylin.service.exporter.impl.GroupColumnDocExporter;
import org.kylin.service.exporter.impl.P5Select3DExporter;
import org.kylin.service.exporter.impl.WCodeKillerDocumentExporter;
import org.kylin.service.pfive.WCodeProcessService;
import org.kylin.util.DocUtils;
import org.kylin.util.WCodeUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class WCodeProcessServiceImpl implements WCodeProcessService{


    @Resource
    private List<Strategy< List<WCode>, WCodeReq>> bitStrategies;

    @Override
    public Optional<WCodeSummarise> sequenceProcess(WCodeReq wCodeReq) {
        if(wCodeReq == null){
            return Optional.empty();
        }
        FilterStrategyEnum filterStrategyEnum = FilterStrategyEnum.getById(wCodeReq.getFilterType());
        if(filterStrategyEnum == null){
            return Optional.empty();
        }

        List<WCode> backupCodes = wCodeReq.getWCodes();

        SequenceProcessor sequenceProcessor = StrategyFactory.createProcessor(filterStrategyEnum);
        if(sequenceProcessor == null){
            return Optional.empty();
        }

        List<WCode> deletedCodes = new ArrayList<>();

        sequenceProcessor.init(wCodeReq);
        List<WCode> wCodes = sequenceProcessor.process(deletedCodes);

        Collections.sort(wCodes);

        if(filterStrategyEnum == FilterStrategyEnum.RANDOM_FILTER){
            WCodeUtils.plusFreq(wCodes);
        }

        WCodeSummarise wCodeSummarise = WCodeUtils.construct(wCodes, filterStrategyEnum.getKey(), deletedCodes, wCodeReq);

        if(filterStrategyEnum == FilterStrategyEnum.EXTEND_AND_SELECT || filterStrategyEnum == FilterStrategyEnum.EXTEND_CODE){
            wCodeSummarise.setBackupCodes(backupCodes);
            if(wCodeSummarise.getExtendCount() == null || wCodeReq.getExtendCount() != null){
                wCodeSummarise.setExtendCount(wCodeReq.getExtendCount());
            }
            if(filterStrategyEnum == FilterStrategyEnum.EXTEND_CODE){
                wCodeSummarise.setExtendRatio(NumberUtils.toInt(wCodeReq.getBoldCodeFive()));
                wCodeSummarise.setwCodes(wCodes.subList(0, wCodes.size() > 1000 ? 1000: wCodes.size()));
            }
        }

        return Optional.of(wCodeSummarise);
    }


    @Override
    public Optional<WCodeSummarise> bitsProcess(WCodeReq wCodeReq) {
        if(wCodeReq == null){
            return Optional.empty();
        }

        List<WCode> wCodes = wCodeReq.getWCodes();
        for(Strategy< List<WCode>, WCodeReq> strategy: bitStrategies) {
            if(strategy.shouldExecute(wCodeReq)) {
                wCodes = strategy.execute(wCodeReq, wCodes);
            }
        }

        Collections.sort(wCodes);

        return Optional.of(WCodeUtils.construct(wCodes,null, null, wCodeReq));
    }


    @Override
    public Optional<String> exportWCodeToFile(WCodeReq wCodeReq) throws IOException{

        Optional<ExportPatternEnum> ep = ExportPatternEnum.getById(wCodeReq.getExportFormat());
        if(!ep.isPresent()){
            return Optional.empty();
        }

        try {
            ExportPatternEnum patternEnum = ep.get();
            if(patternEnum == ExportPatternEnum.NORMAL || patternEnum == ExportPatternEnum.NORMAL_SEQ_NO){
                return Optional.of(DocUtils.saveWCodes(wCodeReq));
            }else if(patternEnum == ExportPatternEnum.HALF_PAGE){
                return Optional.of(DocUtils.saveWCodesHalf(wCodeReq));
            }
        } catch (IOException e) {
            log.info("导出文件错误", e);
            return Optional.empty();
        }

        // 策略导出
        AbstractDocumentExporter exporter;
        DocHolder docHolder = new DocHolder();
        if(ExportPatternEnum.GROUP_COLUMN == ep.get()) {
            exporter = new GroupColumnDocExporter();
        } else if(ExportPatternEnum.P5_SELECT_3D == ep.get()){
            exporter = new P5Select3DExporter();
        }
        else  {
            exporter = new WCodeKillerDocumentExporter();
        }
        try {
            exporter.writeTitleAsDefaultFormat(docHolder, null);
            exporter.writeContentToDoc(docHolder, wCodeReq);
            String fileName = exporter.exportDocAsFile(docHolder);
            return Optional.of(fileName);
        } catch (IOException e) {
            log.warn("导出文件错误", e);
            return Optional.empty();
        } catch (RuntimeException re){
            log.warn("导出文件错误", re);
            return Optional.empty();
        }
    }
}
