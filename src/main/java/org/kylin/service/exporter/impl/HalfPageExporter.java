package org.kylin.service.exporter.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.kylin.bean.p5.WCode;
import org.kylin.bean.p5.WCodeReq;
import org.kylin.constant.ExportPatternEnum;
import org.kylin.service.exporter.AbstractDocumentExporter;
import org.kylin.service.exporter.DocHolder;
import org.kylin.util.DocUtils;
import org.kylin.util.WCodeUtils;
import org.kylin.util.WyfCollectionUtils;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Component
public class HalfPageExporter extends AbstractDocumentExporter {


    @Override
    public void writeContentToDoc(DocHolder docHolder, WCodeReq data) {
        Objects.requireNonNull(docHolder);
        Objects.requireNonNull(data);
        List<WCode> wCodes = data.getwCodes();

        writeStat(docHolder.getDocument().createParagraph(), CollectionUtils.size(wCodes));

        if(CollectionUtils.isEmpty(wCodes)){
            return;
        }

        List<WCode> nonPairCodes = WCodeUtils.filterNonPairCodes(wCodes);

        List<WCode> halfPageCodes = WyfCollectionUtils.getSubList(nonPairCodes, 8, 4);
        if(!CollectionUtils.isEmpty(halfPageCodes)){
            Collections.sort(halfPageCodes);
            String titleString = String.format("排列5码·半页码(非对子 %d 注)", halfPageCodes.size());
            DocUtils.exportWCodes(docHolder.getDocument(), halfPageCodes, titleString, null, false, null, false);
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


    @Override
    public List<ExportPatternEnum> getSupportedExportPatterns() {
        return Arrays.asList(ExportPatternEnum.HALF_PAGE);
    }
}
