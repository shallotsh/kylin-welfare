package org.kylin.service.exporter.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.kylin.bean.W3DCode;
import org.kylin.bean.WelfareCode;
import org.kylin.bean.p5.WCode;
import org.kylin.bean.p5.WCodeReq;
import org.kylin.constant.ExportPatternEnum;
import org.kylin.service.exporter.AbstractDocumentExporter;
import org.kylin.service.exporter.DocHolder;
import org.kylin.util.TransferUtil;
import org.kylin.util.WCodeUtils;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class ExpertRecommendDocExporter extends AbstractDocumentExporter{

    @Override
    public void writeContentToDoc(DocHolder docHolder, WCodeReq data) {
        Objects.requireNonNull(docHolder);
        Objects.requireNonNull(data);

        if(CollectionUtils.isEmpty(data.getWCodes())){
            return;
        }

        List<WCode> wCodes = data.getWCodes();
        Collections.sort(wCodes);

        if(data.getFreqSeted()){
            // 调用新新逻辑输出
            List<WCode> all = new ArrayList<>();
            all.addAll(wCodes);
            all.addAll(data.getDeletedCodes().stream().flatMap(x -> x.getData().stream()).collect(Collectors.toList()));
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


        List<W3DCode> w3DCodes = wCodes.stream().map(this::from).collect(Collectors.toList());
        List<W3DCode> pairCodes = TransferUtil.getPairCodes(w3DCodes);
        String title = String.format("对子 %d 注", pairCodes.size());
        writeW3DCodes(docHolder.getDocument().createParagraph(), pairCodes, title);

        List<W3DCode> nonPairCodes = TransferUtil.getNonPairCodes(w3DCodes);
        title = String.format("非对子 %d 注", nonPairCodes.size());

        writeW3DCodes(docHolder.getDocument().createParagraph(), nonPairCodes, title);

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


    public void writeW3DCodes( XWPFParagraph paragraph, List<W3DCode> w3DCodes, String title){

        if(CollectionUtils.isEmpty(w3DCodes)){
            return;
        }

        writeSubTitle(paragraph, title);

        XWPFRun content = paragraph.createRun();
        content.setFontSize(14);
        paragraph.setAlignment(ParagraphAlignment.LEFT);



        List<W3DCode> codes = w3DCodes.stream().filter(w3DCode -> w3DCode.getFreq()!=0).collect(Collectors.toList());
        boolean printFreq = !CollectionUtils.isEmpty(codes);

        Map<W3DCode, Integer> stat = getW3DCodeIntStat(w3DCodes);
        w3DCodes = new ArrayList<>(stat.keySet());
        Collections.sort(w3DCodes, WelfareCode::bitSort);
        for(W3DCode w3DCode : w3DCodes) {
            String ct;
            if(printFreq) {
                ct = w3DCode.toString();
            }else{
                ct = w3DCode.toString().substring(3,6);
            }
            if(stat.get(w3DCode) > 1){
                ct += "("+ stat.get(w3DCode) +")    ";
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


    private Map<W3DCode, Integer> getW3DCodeIntStat(List<W3DCode> w3DCodes){
        if(CollectionUtils.isEmpty(w3DCodes)){
            return Collections.emptyMap();
        }

        Map<W3DCode, Integer> statMap = new HashMap<>();
        w3DCodes.forEach(code -> {
            Integer count = statMap.getOrDefault(code, 0);
            statMap.put(code, count + 1);
        });

        return statMap;
    }

    private W3DCode from(WCode wCode){
        return new W3DCode(wCode.getCodes().get(0),
                wCode.getCodes().get(1),
                wCode.getCodes().get(2));
    }

    // =============================== 以下是基于WCode导出数据===========================

    private void saveCodesWithFreq(XWPFParagraph paragraph, List<WCode> wCodes, String title){

        if(CollectionUtils.isEmpty(wCodes)){
            return;
        }

        paragraph.setAlignment(ParagraphAlignment.LEFT);
        XWPFRun titleRun = paragraph.createRun();
        titleRun.setFontSize(16);
        titleRun.setBold(true);
        titleRun.setText(title);
        titleRun.addBreak();

        Map<Integer, List<WCode>> freqToCodes = wCodes.stream().collect(Collectors.groupingBy(WCode::getFreq));

        List<Map.Entry<Integer, List<WCode>>> entries = freqToCodes.entrySet().stream().collect(Collectors.toList());
        Collections.sort(entries, Comparator.comparing(Map.Entry::getKey));

        for(Map.Entry<Integer, List<WCode>> entry : entries){
            if(CollectionUtils.isEmpty(entry.getValue())){
                continue;
            }
            XWPFRun content = paragraph.createRun();
            content.setFontSize(14);
            paragraph.setAlignment(ParagraphAlignment.LEFT);
            content.setTextPosition(20);
            int size = entry.getValue().size();
            String subtitle =  "      " + entry.getKey() + " 次("+ (size < 10? "  "+size: ""+size) +"注):  ";
            content.setText(subtitle);
            for(WCode wCode: entry.getValue()){
                content.setText(wCode.getString(false) + "        ");
            }
            content.addBreak();

        }
    }

    @Override
    public List<ExportPatternEnum> getSupportedExportPatterns() {
        return Arrays.asList(ExportPatternEnum.EXPERT_3D);
    }
}
