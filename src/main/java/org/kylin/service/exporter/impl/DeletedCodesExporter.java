package org.kylin.service.exporter.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.kylin.bean.LabelValue;
import org.kylin.bean.p5.WCode;
import org.kylin.bean.p5.WCodeReq;
import org.kylin.constant.Constants;
import org.kylin.constant.ExportPatternEnum;
import org.kylin.service.exporter.AbstractDocumentExporter;
import org.kylin.service.exporter.DocHolder;
import org.kylin.util.DocUtils;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Component
public class DeletedCodesExporter extends AbstractDocumentExporter {

    @Override
    public void writeContentToDoc(DocHolder docHolder, WCodeReq data) {
        Objects.requireNonNull(docHolder);
        Objects.requireNonNull(data);

        List<LabelValue<List<WCode>>> wCodesArray = data.getDeletedCodes();

        if(!Objects.equals(Boolean.TRUE, data.getSavePoint()
                || CollectionUtils.isEmpty(wCodesArray))) {
            return;
        }

        for(int i=0; i<wCodesArray.size(); i++){
            printCodes(docHolder.getDocument().createParagraph(), wCodesArray.get(i), i);
        }
    }

    private void printCodes(XWPFParagraph paragraph, LabelValue<List<WCode>> labelValue, Integer index){

        Objects.requireNonNull(labelValue);

        if(CollectionUtils.isEmpty(labelValue.getData())){
            return;
        }

        List<WCode> wCodes = labelValue.getData();

        DocUtils.writeSubTitle(paragraph, "A" + index + "(操作:" + labelValue.getLabel() + ", 杀码 " + wCodes.size() + " 注): ");

        XWPFRun content = paragraph.createRun();
        content.setFontSize(14);
        paragraph.setAlignment(ParagraphAlignment.LEFT);

        Collections.sort(wCodes);

        for(WCode wCode : wCodes){

            String ct = wCode.getString(false);
            content.setText(ct + Constants.EXPORT_SEPARATOR);
        }
        content.addBreak();
    }

    @Override
    public List<ExportPatternEnum> getSupportedExportPatterns() {
        return Arrays.asList(ExportPatternEnum.DELETED_CODES);
    }
}
