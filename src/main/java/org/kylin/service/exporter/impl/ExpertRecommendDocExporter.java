package org.kylin.service.exporter.impl;

import com.google.common.collect.Maps;
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
import org.kylin.util.CommonUtils;
import org.kylin.util.TransferUtil;
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

        List<W3DCode> w3DCodes = wCodes.stream().map(this::from).collect(Collectors.toList());
        List<W3DCode> pairCodes = TransferUtil.getPairCodes(w3DCodes);
        String title = String.format("对子 %d 注", pairCodes.size());
        writeW3DCodes(docHolder.getDocument().createParagraph(), pairCodes, title);

        List<W3DCode> nonPairCodes = TransferUtil.getNonPairCodes(w3DCodes);
        title = String.format("非对子 %d 注", nonPairCodes.size());

        writeW3DCodes(docHolder.getDocument().createParagraph(), nonPairCodes, title);

    }

    public static void writeSubTitle(XWPFParagraph paragraph, String titleString){

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


    public static void writeW3DCodes( XWPFParagraph paragraph, List<W3DCode> w3DCodes, String title){

        if(CollectionUtils.isEmpty(w3DCodes)){
            return;
        }

        writeSubTitle(paragraph, title);

        XWPFRun content = paragraph.createRun();
        content.setFontSize(14);
        paragraph.setAlignment(ParagraphAlignment.LEFT);

        Collections.sort(w3DCodes, WelfareCode::bitSort);

        List<W3DCode> codes = w3DCodes.stream().filter(w3DCode -> w3DCode.getFreq()!=0).collect(Collectors.toList());
        boolean printFreq = !CollectionUtils.isEmpty(codes);

        for(W3DCode w3DCode : w3DCodes) {
            String ct;
            if(printFreq) {
                ct = w3DCode.toString();
            }else{
                ct = w3DCode.toString().substring(3,6);
            }
            ct += "     ";
            content.setText(ct);
        }

        content.addBreak();
        content.setTextPosition(20);

        XWPFRun sep = paragraph.createRun();
        sep.setTextPosition(50);
    }


    private void writeStats(DocHolder docHolder, ExportPatternEnum exportPatternEnum, Integer p5Size, Integer p3Size) {

        XWPFParagraph header = docHolder.getDocument().createParagraph();
        XWPFRun hr2 = header.createRun();

        hr2.setText(exportPatternEnum.getDesc() + " 共计" + p5Size + "注排列5码!!!提取注数："+ p3Size +" 注  "
                + CommonUtils.getCurrentDateString());
        hr2.setTextPosition(10);
        hr2.setFontSize(18);

        XWPFRun hr3 = header.createRun();
        hr3.setText(" ");
        hr3.addBreak();
    }

    private W3DCode from(WCode wCode){
        return new W3DCode(wCode.getCodes().get(0),
                wCode.getCodes().get(1),
                wCode.getCodes().get(2));
    }


    @Override
    public List<ExportPatternEnum> getSupportedExportPatterns() {
        return Arrays.asList(ExportPatternEnum.EXPERT_3D);
    }
}
