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
import org.kylin.util.WCodeUtils;
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
            LabelValue<List<WCode>> labelValue = wCodesArray.get(i);
//
//            List<WCode> pairCodes = WCodeUtils.filterPairCodes(labelValue.getData());
//            LabelValue<List<WCode>> pairLabelCodes = LabelValue.<List<WCode>>builder().label(labelValue.getLabel()).data(pairCodes).build();
//            printCodes(docHolder.getDocument().createParagraph(), pairLabelCodes, i+1, "对子");

            List<WCode> nonPairCodes = WCodeUtils.filterNonPairCodes(labelValue.getData());
            LabelValue<List<WCode>> nonPairLabelCodes = LabelValue.<List<WCode>>builder().label(labelValue.getLabel()).data(nonPairCodes).build();
            printCodes(docHolder.getDocument().createParagraph(), nonPairLabelCodes, i+1, "非对子");

        }
    }

    private void printCodes(XWPFParagraph paragraph, LabelValue<List<WCode>> labelValue, Integer index, String desc){

        Objects.requireNonNull(labelValue);

        if(CollectionUtils.isEmpty(labelValue.getData())){
            return;
        }

        List<WCode> wCodes = labelValue.getData();

        DocUtils.writeSubTitle(paragraph, "A" + index + "(操作:" + labelValue.getLabel() + ", 杀码" + desc + wCodes.size() + " 注): ");

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
