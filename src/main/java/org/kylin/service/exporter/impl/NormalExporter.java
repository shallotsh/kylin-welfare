package org.kylin.service.exporter.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.xwpf.usermodel.*;
import org.kylin.bean.p5.WCode;
import org.kylin.bean.p5.WCodeReq;
import org.kylin.constant.ExportPatternEnum;
import org.kylin.service.exporter.AbstractDocumentExporter;
import org.kylin.service.exporter.DocHolder;
import org.kylin.util.DocUtils;
import org.kylin.util.WCodeUtils;
import org.kylin.util.WyfCollectionUtils;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

import static org.kylin.util.DocUtils.exportWCodes;

@Component
public class NormalExporter extends AbstractDocumentExporter {

    @Override
    public void writeContentToDoc(DocHolder docHolder, WCodeReq data) {
        Objects.requireNonNull(docHolder);
        Objects.requireNonNull(data);
        List<WCode> wCodes = data.getwCodes();

        writeStat(docHolder.getDocument().createParagraph(), CollectionUtils.size(wCodes));

        if(CollectionUtils.isEmpty(wCodes)){
            return;
        }

        if(data.isRandomKill()){
            randomExport(data, docHolder.getDocument());
        }else{
            normalExport(data, docHolder.getDocument());
        }
    }

    private void writeStat(XWPFParagraph header, Integer codeSize){

        XWPFRun hr2 = header.createRun();

        hr2.setText("共计" + codeSize + "注排列5码!!!");
        hr2.setTextPosition(10);
        hr2.setFontSize(18);

        XWPFRun hr3 = header.createRun();
        hr3.setText(" ");
        hr3.addBreak();
    }


    private static void randomExport(WCodeReq wCodeReq, XWPFDocument doc){
        if(wCodeReq == null || CollectionUtils.isEmpty(wCodeReq.getwCodes()) || doc == null){
            return;
        }

        List<WCode> pairCodes = WCodeUtils.filterPairCodes(wCodeReq.getwCodes());
        String title = "对子( " + CollectionUtils.size(pairCodes) +" 注)";
        exportRandomByType(pairCodes, doc, title);

        List<WCode> nonPairCodes = WCodeUtils.filterNonPairCodes(wCodeReq.getwCodes());
        title = "非对子( " + CollectionUtils.size(nonPairCodes) +" 注)";
        exportRandomByType(nonPairCodes, doc, title);


    }

    private static void exportRandomByType(List<WCode> wCodes, XWPFDocument doc, String title){
        if(CollectionUtils.isEmpty(wCodes) || doc == null){
            return;
        }
        int highestFreq = WCodeUtils.getHighestFreq(wCodes);

        XWPFParagraph header = doc.createParagraph();
        header.setVerticalAlignment(TextAlignment.TOP);
        header.setWordWrap(true);
        header.setAlignment(ParagraphAlignment.LEFT);
        XWPFRun hr1 = header.createRun();
        hr1.setText((title));
        hr1.setBold(true);
        hr1.setFontSize(20);
        hr1.addBreak();

        for(int i=2; i <= highestFreq; i++){
            int freq = i;
            List<WCode> exportRandomCodes = wCodes.stream().filter(wCode -> wCode.getFreq() == freq).collect(Collectors.toList());
            String exportTitle = "频度" + freq + "(注数" + CollectionUtils.size(exportRandomCodes) + ")";
            exportWCodes(doc, exportRandomCodes, exportTitle, null, false, null, false);
        }

        XWPFParagraph headerEnd = doc.createParagraph();
        header.setVerticalAlignment(TextAlignment.TOP);
        header.setWordWrap(true);
        header.setAlignment(ParagraphAlignment.LEFT);
        XWPFRun hrEnd = headerEnd.createRun();
        hrEnd.setBold(true);
        hrEnd.setFontSize(20);
        hrEnd.addBreak();

    }

    private static void normalExport(WCodeReq wCodeReq, XWPFDocument doc){

        List<WCode> nonPairCodes = WCodeUtils.filterNonPairCodes(wCodeReq.getwCodes());

        Optional<ExportPatternEnum> ep = ExportPatternEnum.getById(wCodeReq.getExportFormat());
        boolean isNormalSeqNo =  ep.map( e -> e ==ExportPatternEnum.NORMAL_SEQ_NO ?true:false).orElse(Boolean.FALSE);

        int customRandomCount = 200;
        if(StringUtils.isNumeric(wCodeReq.getRandomCount()) && NumberUtils.toInt(wCodeReq.getRandomCount()) > 0
                && NumberUtils.toInt(wCodeReq.getRandomCount()) < 1000 ){
            customRandomCount = NumberUtils.toInt(wCodeReq.getRandomCount());
        }

        List<WCode> nonPairRandomTenCodes = WyfCollectionUtils.getRandomList(nonPairCodes, 10);
        List<WCode> nonPairRandFiveCodes = WyfCollectionUtils.getRandomList(nonPairCodes, 5);
        List<WCode> nonPairRand200Codes = WyfCollectionUtils.getRandomList(nonPairCodes, customRandomCount);

        String separator = "※※※";

        if(!CollectionUtils.isEmpty(nonPairRandomTenCodes)){
            Collections.sort(nonPairRandomTenCodes);
            String titleString = String.format("排列5码随机·非对子( %d 注)", nonPairRandomTenCodes.size());
            DocUtils.exportWCodes(doc, nonPairRandomTenCodes, titleString, null, wCodeReq.getFreqSeted(),
                    null, isNormalSeqNo);
        }

        if(!CollectionUtils.isEmpty(nonPairRandFiveCodes)){
            Collections.sort(nonPairRandFiveCodes);
            String titleString = String.format("排列5码随机·非对子( %d 注)", nonPairRandFiveCodes.size());
            exportWCodes(doc, nonPairRandFiveCodes, titleString, null, wCodeReq.getFreqSeted(),
                    null,  isNormalSeqNo);
        }


        if(!CollectionUtils.isEmpty(nonPairRand200Codes) && nonPairRand200Codes.size() < CollectionUtils.size(nonPairCodes)){
            Collections.sort(nonPairRand200Codes);
            String titleString = String.format("排列5码随机·非对子( %d 注)", nonPairRand200Codes.size());
            exportWCodes(doc, nonPairRand200Codes, titleString, null, wCodeReq.getFreqSeted(), null,
                    isNormalSeqNo);
        }

        List<WCode> pairCodes = WCodeUtils.filterPairCodes(wCodeReq.getwCodes());
        if(!CollectionUtils.isEmpty(pairCodes)){
            Collections.sort(pairCodes);
            String titleString = String.format("排列5码·对子( %d 注)", pairCodes.size());
            exportWCodes(doc, pairCodes, titleString, separator, wCodeReq.getFreqSeted(),
                    null,  isNormalSeqNo);
        }

        if(!CollectionUtils.isEmpty(nonPairCodes)){
            Collections.sort(nonPairCodes);
            String titleString = String.format("排列5码·非对子( %d 注)", nonPairCodes.size());
            exportWCodes(doc, nonPairCodes, titleString, separator, wCodeReq.getFreqSeted(), null,
                    isNormalSeqNo);
        }
    }


    @Override
    public List<ExportPatternEnum> getSupportedExportPatterns() {
        return Arrays.asList(ExportPatternEnum.NORMAL, ExportPatternEnum.NORMAL_SEQ_NO);
    }
}
