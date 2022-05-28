package org.kylin.service.exporter.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xwpf.usermodel.*;
import org.kylin.bean.p5.WCode;
import org.kylin.bean.p5.WCodeReq;
import org.kylin.constant.ExportPatternEnum;
import org.kylin.service.exporter.AbstractDocumentExporter;
import org.kylin.service.exporter.DocHolder;
import org.kylin.service.exporter.ExportPropertites;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class XCode2DKillerDocExporter extends AbstractDocumentExporter{

    @Override
    public void writeContentToDoc(DocHolder docHolder, WCodeReq data) {
        Objects.requireNonNull(docHolder);
        Objects.requireNonNull(data);
        writeBody(docHolder, data);
    }

    private void writeBody(DocHolder docHolder, WCodeReq data) {
        Boolean freqSetted = data.getFreqSeted();

        int count = (CollectionUtils.size(data.getwCodes()));

        XWPFDocument doc = docHolder.getDocument();

        // ab*
        exportWCodes(doc, data.getwCodes(), "ab* ( " + count + " 注 )", null, freqSetted, "ab*", docHolder.getExportPropertites());

        // a*b
        exportWCodes(doc, data.getwCodes(), "a*b ( " + count + " 注 )", null, freqSetted, "a*b", docHolder.getExportPropertites());

        // *ab
        exportWCodes(doc, data.getwCodes(), "*ab ( " + count + " 注 )", null, freqSetted, "*ab", docHolder.getExportPropertites());

    }

    private  void exportWCodes(XWPFDocument doc, List<WCode> wCodes, String titleString, String separator, Boolean freqSeted, String pattern, ExportPropertites exportPropertites){

        if(CollectionUtils.isEmpty(wCodes)){
            return;
        }

        Collections.sort(wCodes);

        // 输出小标题
        XWPFParagraph paragraph = doc.createParagraph();
        if(!StringUtils.isBlank(titleString)){
            XWPFRun title = paragraph.createRun();
            paragraph.setAlignment(ParagraphAlignment.LEFT);
            title.setFontSize(16);
            title.setBold(true);
            title.setText(titleString);
            title.addBreak();
        }

        // 输出段落分割线
        XWPFRun hr = paragraph.createRun();
        hr.setFontSize(10);
        hr.setText("+++++++++++++++++++++++++++++++++++++++++++");
        hr.addBreak();



        XWPFRun content = paragraph.createRun();
        content.setFontSize(14);

        Map<WCode, Integer> stat = getXCodeIntStat(wCodes);
        wCodes = new ArrayList<>(stat.keySet());
        Collections.sort(wCodes);
        String span = "     ";

        for(WCode code : wCodes) {

            String printCode = "";
            if("ab*".equals(pattern)) {
                printCode =  ""+code.getCodes().get(0)+code.getCodes().get(1)+"*";
            }else if("ba*".equals(pattern)){
                printCode = ""+code.getCodes().get(1)+code.getCodes().get(0)+"*";
            }else if("*ab".equals(pattern)){
                printCode = ""+"*"+code.getCodes().get(0)+code.getCodes().get(1);
            }else if("*ba".equals(pattern)){
                printCode = "*"+code.getCodes().get(1)+code.getCodes().get(0);
            }else if("a*b".equals(pattern)){
                printCode = ""+code.getCodes().get(0)+"*"+code.getCodes().get(1);
            }else if("b*a".equals(pattern)){
                printCode = ""+code.getCodes().get(1)+"*"+code.getCodes().get(0);
            }

//            if(stat.get(code) > 1){
//                printCode += "("+ stat.get(code) + ")" + "     ";
//            }

            if(freqSeted != null && freqSeted){
                if(exportPropertites == null
                        || code.getFreq() >= exportPropertites.getFreqLowLimitValue()) {
                    content.setText(printCode);
                    content.setText("(" + code.getFreq() + ")");
                }else{
                    continue;
                }
            }else{
                content.setText(printCode);
            }
            content.setText(span);

        }

        content.addBreak();
        content.setTextPosition(20);

        XWPFRun sep = paragraph.createRun();
        sep.setTextPosition(50);
    }

    private Map<WCode, Integer> getXCodeIntStat(List<WCode> xCodes){
        if(CollectionUtils.isEmpty(xCodes)){
            return Collections.emptyMap();
        }

        Map<WCode, Integer> statMap = new HashMap<>();
        xCodes.forEach(code -> {
            Integer count = statMap.getOrDefault(code, 0);
            statMap.put(code, count + 1);
        });

        return statMap;
    }

    @Override
    public List<ExportPatternEnum> getSupportedExportPatterns() {
        return Arrays.asList(ExportPatternEnum.LOCATION_2D);
    }
}
