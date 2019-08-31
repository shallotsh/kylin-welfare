package org.kylin.service.exporter.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.kylin.bean.p2.XCodeReq;
import org.kylin.bean.p5.WCode;
import org.kylin.bean.p5.WCodeReq;
import org.kylin.service.exporter.AbstractDocumentExporter;
import org.kylin.service.exporter.DocHolder;
import org.kylin.util.CommonUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class XCode2DKillerDocExporter extends AbstractDocumentExporter{

    @Override
    public void writeContentToDoc(DocHolder docHolder, WCodeReq data) {
        Objects.requireNonNull(docHolder);
        Objects.requireNonNull(data);
        writeStats(docHolder);
        writeBody(docHolder, data);
    }


    private void writeStats(DocHolder docHolder) {

        XWPFParagraph header = docHolder.getDocument().createParagraph();
        XWPFRun hr2 = header.createRun();

        hr2.setText("时间：" + CommonUtils.getCurrentDateString());
        hr2.setTextPosition(10);
        hr2.setFontSize(18);

        XWPFRun hr3 = header.createRun();
        hr3.setText(" ");
        hr3.addBreak();

    }


    private void writeBody(DocHolder docHolder, WCodeReq data) {
        Boolean freqSeted = data.getFreqSeted();

        int count = (CollectionUtils.size(data.getwCodes()));

        XWPFDocument doc = docHolder.getDocument();

        // ab*
        exportWCodes(doc, data.getwCodes(), "ab* : " + count + " 注", null, freqSeted, "ab*");

        // ba*
        exportWCodes(doc, data.getwCodes(), "ba*: " + count + " 注" , null, freqSeted, "ba*");

        // *ab
        exportWCodes(doc, data.getwCodes(), "*ab: " + count + " 注", null, freqSeted, "*ab");

        // *ba
        exportWCodes(doc, data.getwCodes(), "*ba: "  + count + " 注", null, freqSeted, "*ba");

        // a*b
        exportWCodes(doc, data.getwCodes(), "a*b: "  + count + " 注", null, freqSeted, "a*b");

        // b*a
        exportWCodes(doc, data.getwCodes(), "b*a: "  + count + " 注", null, freqSeted, "b*a");



    }

    private  void exportWCodes(XWPFDocument doc, List<WCode> wCodes, String titleString, String separator, Boolean freqSeted, String pattern){

        if(CollectionUtils.isEmpty(wCodes)){
            return;
        }

        Collections.sort(wCodes);

        XWPFParagraph paragraph = doc.createParagraph();
        if(!StringUtils.isBlank(titleString)){
            XWPFRun title = paragraph.createRun();
            paragraph.setAlignment(ParagraphAlignment.LEFT);
            title.setFontSize(18);
            title.setBold(true);
            title.setText(titleString);
            title.addBreak();
        }

        XWPFRun hr = paragraph.createRun();
        hr.setFontSize(10);
        hr.setText("----------------------------------------");
        hr.addBreak();

        XWPFRun content = paragraph.createRun();
        content.setFontSize(14);

        for(WCode code : wCodes) {

            String printCode = "";
            if("ab*".equals(pattern)) {
                printCode =  ""+code.getCodes().get(0)+code.getCodes().get(1)+"*"+ "     ";
            }else if("ba*".equals(pattern)){
                printCode = ""+code.getCodes().get(1)+code.getCodes().get(0)+"*"+ "     ";
            }else if("*ab".equals(pattern)){
                printCode = ""+"*"+code.getCodes().get(0)+code.getCodes().get(1)+ "     ";
            }else if("*ba".equals(pattern)){
                printCode = "*"+code.getCodes().get(1)+code.getCodes().get(0)+ "     ";
            }else if("a*b".equals(pattern)){
                printCode = ""+code.getCodes().get(0)+"*"+code.getCodes().get(1)+ "     ";
            }else if("b*a".equals(pattern)){
                printCode = ""+code.getCodes().get(1)+"*"+code.getCodes().get(0)+ "     ";
            }

            if(freqSeted != null && freqSeted){
                printCode = "[" + code.getFreq() + "]" + printCode;
            }

            content.setText(printCode);

        }

        content.addBreak();
        content.setTextPosition(20);

        XWPFRun sep = paragraph.createRun();
        sep.setTextPosition(50);
    }
}
