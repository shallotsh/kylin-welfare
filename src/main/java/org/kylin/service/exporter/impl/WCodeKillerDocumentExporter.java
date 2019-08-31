package org.kylin.service.exporter.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.kylin.bean.p5.WCode;
import org.kylin.bean.p5.WCodeReq;
import org.kylin.constant.ExportPatternEnum;
import org.kylin.constant.FilterStrategyEnum;
import org.kylin.service.exporter.AbstractDocumentExporter;
import org.kylin.service.exporter.DocHolder;
import org.kylin.util.CommonUtils;
import org.kylin.util.DocUtils;
import org.kylin.util.WCodeUtils;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class WCodeKillerDocumentExporter extends AbstractDocumentExporter{


    @Override
    public void writeContentToDoc(DocHolder docHolder, WCodeReq data) {
        Objects.requireNonNull(docHolder);
        Objects.requireNonNull(data);
        List<WCode> targetCodes = getTargetCodes(data);
        if(CollectionUtils.isEmpty(targetCodes)){
            return;
        }

        Optional<ExportPatternEnum> ep = ExportPatternEnum.getById(data.getExportFormat());
        FilterStrategyEnum filterStrategyEnum  = ep.map(e -> FilterStrategyEnum.getById(e.getId()))
                .orElseThrow(RuntimeException::new);

        writeStats(docHolder, filterStrategyEnum, targetCodes.size());
        writeBody(docHolder, targetCodes, filterStrategyEnum, data.getFreqSeted(), ep.get());
    }


    public List<WCode> getTargetCodes(WCodeReq wCodeReq){
        Optional<ExportPatternEnum> ep = ExportPatternEnum.getById(wCodeReq.getExportFormat());
        if( Objects.isNull(wCodeReq.getDeletedCodesPair()) || !ep.isPresent()){
            throw new RuntimeException("导出者数据为空");
        }

        FilterStrategyEnum filterStrategyEnum = FilterStrategyEnum.getById(ep.get().getId());
        return Optional.ofNullable(filterStrategyEnum)
                .map(strategy ->  getDeleteCodes(wCodeReq, strategy.getKey())).get();
    }

    private List<WCode> getDeleteCodes(WCodeReq wCodeReq, String key){
        return wCodeReq.getDeletedCodesPair().get(key);
    }

    private void writeStats(DocHolder docHolder,FilterStrategyEnum filterStrategyEnum, Integer codesSize) {

        XWPFParagraph header = docHolder.getDocument().createParagraph();
        XWPFRun hr2 = header.createRun();

        hr2.setText(filterStrategyEnum.getDesc() + " 共计" + codesSize + "注排列5码!!!     时间："
                + CommonUtils.getCurrentDateString());
        hr2.setTextPosition(10);
        hr2.setFontSize(18);

        XWPFRun hr3 = header.createRun();
        hr3.setText(" ");
        hr3.addBreak();
    }

    private void writeBody(DocHolder docHolder,
                           List<WCode> wCodes,
                           FilterStrategyEnum filterStrategyEnum,
                           Boolean isFreqSet,
                           ExportPatternEnum exportPatternEnum) {

        Collections.sort(wCodes);
        List<WCode> pairCodes = WCodeUtils.filterPairCodes(wCodes);
        List<WCode> nonPairCodes = WCodeUtils.filterNonPairCodes(wCodes);


        if(!CollectionUtils.isEmpty(pairCodes)) {
            String titleString = String.format(filterStrategyEnum.getDesc() + " 对子( %d 注)", pairCodes.size());
            DocUtils.exportWCodes(docHolder.getDocument(),
                    pairCodes,
                    titleString,
                    null,
                    isFreqSet,
                    null,
                    exportPatternEnum == ExportPatternEnum.NORMAL_SEQ_NO ? true: false);
        }

        if(!CollectionUtils.isEmpty(nonPairCodes)) {
            String titleString = String.format(filterStrategyEnum.getDesc() + " 非对子( %d 注)", nonPairCodes.size());
            DocUtils.exportWCodes(docHolder.getDocument(),
                    nonPairCodes,
                    titleString,
                    null,
                    isFreqSet,
                    null,
                    exportPatternEnum == ExportPatternEnum.NORMAL_SEQ_NO ? true: false);
        }

    }

    @Override
    public List<ExportPatternEnum> getSupportedExportPatterns() {
        return Arrays.asList(ExportPatternEnum.BIG_SUM_KILL,
                ExportPatternEnum.CONTAIN_FOUR_KILL,
                ExportPatternEnum.CONTAIN_FIVE_KILL);
    }
}
