package org.kylin.service.exporter.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xwpf.usermodel.LineSpacingRule;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.kylin.bean.p5.WCode;
import org.kylin.bean.p5.WCodeReq;
import org.kylin.constant.ExportPatternEnum;
import org.kylin.service.exporter.AbstractDocumentExporter;
import org.kylin.service.exporter.DocHolder;
import org.kylin.util.ExporterControlUtil;
import org.kylin.util.WCodeUtils;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class W3DCommonDocExporter extends AbstractDocumentExporter{

    @Override
    public void writeContentToDoc(DocHolder docHolder, WCodeReq data) {
        Objects.requireNonNull(docHolder);
        Objects.requireNonNull(data);

        if(CollectionUtils.isEmpty(data.getWCodes())){
            return;
        }

        List<WCode> wCodes = data.getWCodes();
        Collections.sort(wCodes);

        if(Objects.equals(Boolean.TRUE, data.getFreqSeted())){
            // 调用新新逻辑输出，3d2和频度法
            List<WCode> all = new ArrayList<>();
            all.addAll(wCodes);
            if(CollectionUtils.isNotEmpty(data.getDeletedCodes())) {
                all.addAll(data.getDeletedCodes().stream().flatMap(x -> x.getData().stream()).collect(Collectors.toList()));
            }
            // 导出对子
            List<WCode> pairCodes = WCodeUtils.filterPairCodes(all);
            String title = String.format("    对子: (%d 注)", pairCodes.size());
            saveCodesWithFreq(docHolder.getDocument().createParagraph(), pairCodes, title);

            // 导出非对子
            List<WCode> nonPairCodes = WCodeUtils.filterNonPairCodes(all);
            title = String.format("非对子: (%d 注)", nonPairCodes.size());
            saveCodesWithFreq(docHolder.getDocument().createParagraph(), nonPairCodes, title);
            return ;
        }


        List<WCode> pairCodesNew = WCodeUtils.filterPairCodes(wCodes);
        String title = String.format("对子 %d 注", pairCodesNew.size());
        writeWCodesRefactor(docHolder.getDocument().createParagraph(), pairCodesNew, title);

        List<WCode> nonPairCodesNew = WCodeUtils.filterNonPairCodes(wCodes);
        title = String.format("非对子 %d 注", nonPairCodesNew.size());
        writeWCodesRefactor(docHolder.getDocument().createParagraph(), nonPairCodesNew, title);

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

        XWPFRun hr = paragraph.createRun();
        hr.setFontSize(10);
        hr.setText("----------------------------------------");
        hr.addBreak();

        paragraph.setWordWrap(true);

    }

    private Map<WCode, Integer> getW3DCodeIntStatRefactor(List<WCode> wCodes){
        if(CollectionUtils.isEmpty(wCodes)){
            return Collections.emptyMap();
        }

        Map<WCode, Integer> statMap = new HashMap<>();
        wCodes.forEach(code -> {
            Integer count = statMap.getOrDefault(code, 0);
            statMap.put(code, count + 1);
        });

        return statMap;
    }

    // =============================== 以下是基于WCode导出数据===========================



    public void writeWCodesRefactor( XWPFParagraph paragraph, List<WCode> wCodes, String title){
        if(CollectionUtils.isEmpty(wCodes)){
            return;
        }
        paragraph.setSpacingBetween(1.2, LineSpacingRule.AUTO);

        writeSubTitle(paragraph, title);

        XWPFRun content = paragraph.createRun();
        content.setFontSize(14);
        paragraph.setAlignment(ParagraphAlignment.LEFT);

        boolean printFreq = wCodes.stream().anyMatch(w3DCode -> w3DCode.getFreq()!=0);

        Map<WCode, Integer> stat = getW3DCodeIntStatRefactor(wCodes);

        List<WCode> temp = new ArrayList<>(stat.keySet());
        Collections.sort(temp, WCode::compareByTailNo);
        for(WCode wCode : temp) {
            // 修改专家组码法输出
            String ct = wCode.getStringWithTailSum();
            if(stat.get(wCode) > 1){
                ct += "("+ stat.get(wCode) +")    ";
            }else {
                ct += "        ";
            }
            content.setText(ct);
        }

        content.addBreak();
        content.setTextPosition(20);

        XWPFRun sep = paragraph.createRun();
        sep.setTextPosition(50);
    }


    private void saveCodesWithFreq(XWPFParagraph paragraph, List<WCode> wCodes, String title){

        if(CollectionUtils.isEmpty(wCodes)){
            return;
        }

        paragraph.setSpacingBetween(1.2, LineSpacingRule.AUTO);

        paragraph.setAlignment(ParagraphAlignment.LEFT);
        XWPFRun titleRun = paragraph.createRun();
        titleRun.setFontSize(16);
        titleRun.setBold(true);
        titleRun.setText(title);
        titleRun.addBreak();

        Map<Integer, List<WCode>> freqToCodes = wCodes.stream().collect(Collectors.groupingBy(WCode::getFreq));

        List<Map.Entry<Integer, List<WCode>>> entries = freqToCodes.entrySet().stream().collect(Collectors.toList());
        Collections.sort(entries, Map.Entry.comparingByKey());
        if(ExporterControlUtil.getPatternType() == ExportPatternEnum.BIN_SUM_FREQ_3D) {
            Collections.reverse(entries);
        }

        for(Map.Entry<Integer, List<WCode>> entry : entries){
            if(CollectionUtils.isEmpty(entry.getValue())){
                continue;
            }
            XWPFRun content = paragraph.createRun();
            content.setFontSize(14);
            paragraph.setAlignment(ParagraphAlignment.LEFT);
            content.setTextPosition(20);
            int size = entry.getValue().size();
            if(ExporterControlUtil.getPatternType() != ExportPatternEnum.BIN_SUM_FREQ_3D) {
                String subtitle = "      " + entry.getKey() + " 次(" + (size < 10 ? "  " + size : "" + size) + "注):  ";
                content.setText(subtitle);
                List<WCode> tmp = entry.getValue();
                Collections.sort(tmp,  WCode::compareByTailNo);
                for(WCode wCode: tmp){
                    content.setText(wCode.getStringWithFreqAndTailSum() + "        ");
                }
                content.addBreak();
            }else{
                // 3D二和频度法 输出
                for(WCode wCode: entry.getValue()){
                    content.setText(wCode.getStringWithFreqAndTailSum() + "        ");
                }
            }

        }
    }

    @Override
    public List<ExportPatternEnum> getSupportedExportPatterns() {
        return Arrays.asList(ExportPatternEnum.EXPERT_3D, ExportPatternEnum.BIN_SUM_FREQ_3D, ExportPatternEnum.TWO_DERIVE_THREE_CODE);
    }
}
