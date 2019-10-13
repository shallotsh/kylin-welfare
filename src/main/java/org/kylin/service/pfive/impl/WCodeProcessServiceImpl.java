package org.kylin.service.pfive.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.kylin.algorithm.strategy.SequenceProcessor;
import org.kylin.algorithm.strategy.Strategy;
import org.kylin.bean.LabelValue;
import org.kylin.bean.p5.WCode;
import org.kylin.bean.p5.WCodeReq;
import org.kylin.bean.p5.WCodeSummarise;
import org.kylin.constant.ExportPatternEnum;
import org.kylin.constant.FilterStrategyEnum;
import org.kylin.factory.StrategyFactory;
import org.kylin.service.exporter.DocHolder;
import org.kylin.service.exporter.ExportToolSelector;
import org.kylin.service.exporter.IDocExportTool;
import org.kylin.service.pfive.WCodeProcessService;
import org.kylin.util.DocUtils;
import org.kylin.util.WCodeUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;

@Service
@Slf4j
public class WCodeProcessServiceImpl implements WCodeProcessService{


    @Resource
    private List<Strategy< List<WCode>, WCodeReq>> bitStrategies;

    @Resource
    private ExportToolSelector exportToolSelector;

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


        // 保存已删除的3D码，以便导出
        if(Objects.equals(Boolean.TRUE, wCodeReq.getSavePoint())){
            List<LabelValue<List<WCode>>> delCodes = wCodeReq.getDeletedCodes();
            List<WCode> deletedWCodes = WCodeUtils.minus(backupCodes, wCodes);
            if(CollectionUtils.isNotEmpty(deletedWCodes)){
                if(CollectionUtils.isEmpty(deletedCodes)){
                    delCodes = new ArrayList<>();
                }
                delCodes.add(LabelValue.<List<WCode>>builder()
                        .label(filterStrategyEnum.getDesc())
                        .data(deletedWCodes)
                        .build());
            }
            if(CollectionUtils.isNotEmpty(delCodes)){
                wCodeSummarise.setDeletedCodes(delCodes);
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
        List<WCode> backupCodes = new ArrayList<>(wCodes);

        for(Strategy< List<WCode>, WCodeReq> strategy: bitStrategies) {
            if(strategy.shouldExecute(wCodeReq)) {
                wCodes = strategy.execute(wCodeReq, wCodes);
            }
        }

        Collections.sort(wCodes);

        WCodeSummarise summarise = WCodeUtils.construct(wCodes,null, null, wCodeReq);

        // 保存已删除的3D码，以便导出
        if(Objects.equals(Boolean.TRUE, wCodeReq.getSavePoint())){
            List<LabelValue<List<WCode>>> deletedCodes = wCodeReq.getDeletedCodes();
            List<WCode> deletedWCodes = WCodeUtils.minus(backupCodes, wCodes);
            if(CollectionUtils.isNotEmpty(deletedWCodes)){
                if(CollectionUtils.isEmpty(deletedCodes)){
                    deletedCodes = new ArrayList<>();
                }
                deletedCodes.add(LabelValue.<List<WCode>>builder()
                        .label("位杀")
                        .data(deletedWCodes)
                        .build());
            }
            if(CollectionUtils.isNotEmpty(deletedCodes)){
                summarise.setDeletedCodes(deletedCodes);
            }
        }

        return Optional.ofNullable(summarise);
    }


    @Override
    public Optional<String> exportWCodeToFile(WCodeReq wCodeReq) throws IOException{

        Optional<ExportPatternEnum> ep = ExportPatternEnum.getById(wCodeReq.getExportFormat());
        if(!ep.isPresent()){
            return Optional.empty();
        }
        // 策略导出
        Optional<IDocExportTool> iDocExportToolOptional =  exportToolSelector.getByExportPattern(ep.get());

        if(iDocExportToolOptional.isPresent()) {
            DocHolder docHolder = new DocHolder();
            iDocExportToolOptional.get().writeTitleAsDefaultFormat(docHolder, ep.get().getTitle());
            iDocExportToolOptional.get().writeContentToDoc(docHolder, wCodeReq);
            String fileName = iDocExportToolOptional.get().exportDocAsFile(docHolder);
            return Optional.of(fileName);
        }else{
            log.error("没有查询到对应的导出工具 exportPattern:{}", ep.get());
            return Optional.empty();
        }
    }
}
