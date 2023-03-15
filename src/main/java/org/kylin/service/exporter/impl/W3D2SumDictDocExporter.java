package org.kylin.service.exporter.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.util.StringUtil;
import org.apache.poi.wp.usermodel.HeaderFooterType;
import org.apache.poi.xwpf.usermodel.LineSpacingRule;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.kylin.bean.p5.WCode;
import org.kylin.bean.p5.WCodeReq;
import org.kylin.constant.ExportPatternEnum;
import org.kylin.service.exporter.AbstractDocumentExporter;
import org.kylin.service.exporter.DocHolder;
import org.kylin.util.WCodeUtils;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class W3D2SumDictDocExporter extends AbstractDocumentExporter{

    @Override
    public void writeContentToDoc(DocHolder docHolder, WCodeReq data) {
        Objects.requireNonNull(docHolder);
        Objects.requireNonNull(data);

        if(CollectionUtils.isEmpty(data.getWCodes())){
            return;
        }

        List<WCode> wCodes = data.getWCodes();
        Collections.sort(wCodes);

//        if(Objects.equals(Boolean.TRUE, data.getFreqSeted())){
//            // 调用新新逻辑输出
//            List<WCode> all = new ArrayList<>();
//            all.addAll(wCodes);
//            if(CollectionUtils.isNotEmpty(data.getDeletedCodes())) {
//                all.addAll(data.getDeletedCodes().stream().flatMap(x -> x.getData().stream()).collect(Collectors.toList()));
//            }
//            // 导出对子
//            List<WCode> pairCodes = WCodeUtils.filterPairCodes(all);
//            String title = String.format("    对子: (%d 注)", pairCodes.size());
//            saveCodesWithFreq(docHolder.getDocument().createParagraph(), pairCodes, title);
//
//            // 导出非对子
//            List<WCode> nonPairCodes = WCodeUtils.filterNonPairCodes(all);
//            title = String.format("非对子: (%d 注)", nonPairCodes.size());
//            saveCodesWithFreq(docHolder.getDocument().createParagraph(), nonPairCodes, title);
//            return ;
//        }

        // 0315 取消分类导出
        // List<WCode> pairCodesNew = WCodeUtils.filterPairCodes(wCodes);
        // String title = String.format("对子( %d 注 )：", pairCodesNew.size());
        // writeWCodesRefactor(docHolder.getDocument().createParagraph(), pairCodesNew, title);

        // List<WCode> nonPairCodesNew = WCodeUtils.filterNonPairCodes(wCodes);
        // title = String.format("非对子( %d 注 )：", nonPairCodesNew.size());
        // writeWCodesRefactor(docHolder.getDocument().createParagraph(), nonPairCodesNew, title);


        // if(StringUtils.isNotBlank(titleLocal.get())) {
        //     XWPFParagraph headerParagraph = docHolder.getDocument().createHeader(HeaderFooterType.DEFAULT).createParagraph();
        //     XWPFRun run = headerParagraph.createRun();
        //     run.setFontSize(8);
        //     run.setText(titleLocal.get());
        // }

        // 新增去重结果导出
        List<WCode> distinctCodes = WCodeUtils.mergeCodes(wCodes, false);
        List<WCode> distinctPairCodesNew = WCodeUtils.filterPairCodes(distinctCodes);
        XWPFParagraph paragraph = docHolder.getDocument().createParagraph();
        paragraph.setPageBreak(true);
        title = String.format("对子( %d 注 )：", distinctPairCodesNew.size());
        writeWCodesRefactorWithoutBinCode(paragraph, distinctPairCodesNew, title);
        List<WCode> distinctNonPairCodesNew = WCodeUtils.filterNonPairCodes(distinctCodes);
        title = String.format("非对子( %d 注 )：", distinctNonPairCodesNew.size());
        writeWCodesRefactorWithoutBinCode(docHolder.getDocument().createParagraph(), distinctNonPairCodesNew, title);
    }

    public void writeSubTitle(XWPFParagraph paragraph, String titleString){

        if(StringUtils.isBlank(titleString)) {
            return;
        }

        paragraph.setAlignment(ParagraphAlignment.LEFT);
        XWPFRun title = paragraph.createRun();
        title.setFontSize(16);
        title.setBold(true);
        title.setText(titleString);
        title.addBreak();
    }


    /**
     * 不按字典二码和输出
     * @param paragraph
     * @param wCodes
     * @param title
     */
    public void writeWCodesRefactorWithoutBinCode( XWPFParagraph paragraph, List<WCode> wCodes, String title){
        if(CollectionUtils.isEmpty(wCodes)){
            return;
        }
        paragraph.setSpacingBetween(1.2, LineSpacingRule.AUTO);
        writeSubTitle(paragraph, title);

        XWPFRun content = paragraph.createRun();
        content.setFontSize(14);
        paragraph.setAlignment(ParagraphAlignment.LEFT);

        Collections.sort(wCodes, WCode::compareByTailNo);

        for(WCode wCode : wCodes) {
            content.setText(wCode.getStringWithTailSum() + "     ");
        }
        content.addBreak();


        content.addBreak();
        content.setTextPosition(20);

        XWPFRun sep = paragraph.createRun();
        sep.setTextPosition(50);
    }

    public void
    writeWCodesRefactor( XWPFParagraph paragraph, List<WCode> wCodes, String title){
        if(CollectionUtils.isEmpty(wCodes)){
            return;
        }
        writeSubTitle(paragraph, title);

        paragraph.setSpacingBetween(1.2, LineSpacingRule.AUTO);

        XWPFRun content = paragraph.createRun();
        content.setFontSize(14);
        paragraph.setAlignment(ParagraphAlignment.LEFT);

        Map<Integer, List<WCode>> binSumToWCodes = wCodes.stream().collect(Collectors.groupingBy(WCode::getBinSumValue));
        List<Map.Entry<Integer, List<WCode>>> entryList = binSumToWCodes.entrySet().stream().collect(Collectors.toList());
        Collections.sort(entryList, Map.Entry.comparingByKey());
        for(Map.Entry<Integer, List<WCode>> entry : entryList){
            content.setBold(true);
            content.setText("     " +  entry.getKey() + " :     ");
            content.setBold(false);
            for(WCode wCode : entry.getValue()) {
                content.setText(wCode.getStringWithTailSum() + "     ");
            }
            content.addBreak();
        }

//        List<Integer> binSumValues = new ArrayList<>(binSumToWCodes.keySet()).stream().sorted().collect(Collectors.toList());

//        for(Integer binSumValue : binSumValues) {
//            content.setBold(true);
//            content.setText("     " +  binSumValue + " :     ");
//            content.setBold(false);
//            List<WCode> wCodeList = binSumToWCodes.get(binSumValue);
//            for(WCode wCode : wCodeList) {
//                content.setText(wCode.getStringWithTailSum() + "     ");
//            }
//            content.addBreak();
//        }

        content.addBreak();
        content.setTextPosition(20);

        XWPFRun sep = paragraph.createRun();
        sep.setTextPosition(50);
    }

    @Override
    public List<ExportPatternEnum> getSupportedExportPatterns() {
        return Arrays.asList(ExportPatternEnum.BIN_SUM_DICT_3D);
    }
}
