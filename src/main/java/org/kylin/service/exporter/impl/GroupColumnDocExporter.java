package org.kylin.service.exporter.impl;

import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.kylin.bean.p5.WCode;
import org.kylin.bean.p5.WCodeReq;
import org.kylin.constant.ExportPatternEnum;
import org.kylin.service.exporter.AbstractDocumentExporter;
import org.kylin.service.exporter.DocHolder;
import org.kylin.util.CommonUtils;
import org.kylin.util.DocUtils;
import org.kylin.util.WCodeUtils;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class GroupColumnDocExporter extends AbstractDocumentExporter {

    private Set<Integer> columnIdxs = new HashSet<>(Arrays.asList(0));
    private int groupSize = 13;

    @Override
    public void writeContentToDoc(DocHolder docHolder, WCodeReq data) {
        Objects.requireNonNull(docHolder);
        Objects.requireNonNull(data);
        List<WCode> targetCodes = getTargetCodes(data.getWCodes());
        if(CollectionUtils.isEmpty(targetCodes)) return;
        Optional<ExportPatternEnum> exportPatternEnum = ExportPatternEnum.getById(data.getExportFormat());
        exportPatternEnum.ifPresent(ep -> {
            writeStats(docHolder, targetCodes, ep);
            writeBody(docHolder, targetCodes, ep, data.getFreqSeted());
        });

    }

    public List<WCode> getTargetCodes(List<WCode> oriCodes) {
        if(CollectionUtils.isEmpty(oriCodes)){
            return Collections.emptyList();
        }
        List<WCode> wCodes = new ArrayList<>();
        List<List<WCode>> codeArray = Lists.partition(oriCodes, 13);
        for(List<WCode> wCodeList : codeArray){
            for(int i=0; i<wCodeList.size(); i++){
                if(columnIdxs.contains(i)){
                    wCodes.add(wCodeList.get(i));
                }

            }
        }
        return wCodes;
    }

    public void writeStats(DocHolder docHolder, List<WCode> wCodes, ExportPatternEnum exportPattern) {

        Objects.requireNonNull(exportPattern);

        String statDesc = exportPattern.getDesc() + " 共计" + CollectionUtils.size(wCodes)+ "注排列5码!!!";

        XWPFParagraph header = docHolder.getDocument().createParagraph();
        XWPFRun hr2 = header.createRun();
        hr2.setBold(false);
        hr2.setText(exportPattern.getDesc() + statDesc);
        hr2.setTextPosition(10);
        hr2.setFontSize(16);

        XWPFRun hr3 = header.createRun();
        hr3.setText(" ");
        hr3.addBreak();
    }

    public void writeBody(DocHolder docHolder, List<WCode> wCodes, ExportPatternEnum exportPattern, Boolean isSetFreq) {

        Objects.requireNonNull(exportPattern);

        List<WCode> pairCodes = WCodeUtils.filterPairCodes(wCodes);
        List<WCode> nonPairCodes = WCodeUtils.filterNonPairCodes(wCodes);


        if(!CollectionUtils.isEmpty(pairCodes)) {
            String titleString = String.format(exportPattern.getDesc() + " 对子( %d 注)", pairCodes.size());
            DocUtils.exportWCodes(docHolder.getDocument(),
                    pairCodes,
                    titleString,
                    null,
                    isSetFreq,
                    null,
                    exportPattern == ExportPatternEnum.NORMAL_SEQ_NO ? true: false);
        }

        if(!CollectionUtils.isEmpty(nonPairCodes)) {
            String titleString = String.format(exportPattern.getDesc() + " 非对子( %d 注)", nonPairCodes.size());
            DocUtils.exportWCodes(docHolder.getDocument(),
                    nonPairCodes,
                    titleString,
                    null,
                    isSetFreq,
                    null,
                    exportPattern == ExportPatternEnum.NORMAL_SEQ_NO ? true: false);
        }
    }

    @Override
    public List<ExportPatternEnum> getSupportedExportPatterns() {
        return Arrays.asList(ExportPatternEnum.GROUP_COLUMN);
    }
}
