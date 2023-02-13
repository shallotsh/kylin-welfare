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
public class W4DDocExporter extends AbstractDocumentExporter{

    @Override
    public void writeContentToDoc(DocHolder docHolder, WCodeReq data) {
        Objects.requireNonNull(docHolder);
        Objects.requireNonNull(data);

        if(CollectionUtils.isEmpty(data.getWCodes())){
            return;
        }

        List<WCode> wCodes = data.getWCodes();
        Collections.sort(wCodes);
        List<WCode> all = new ArrayList<>();
        all.addAll(wCodes);
        // 兼容逻辑
        if(CollectionUtils.isNotEmpty(data.getDeletedCodes())) {
            all.addAll(data.getDeletedCodes().stream().flatMap(x -> x.getData().stream()).collect(Collectors.toList()));
        }

        // 导出对子
        String title = String.format("%d 组:",wCodes.size());
        saveCodesWithFreq(docHolder.getDocument().createParagraph(), wCodes, title);

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

        XWPFRun content = paragraph.createRun();
        content.setFontSize(14);
        paragraph.setAlignment(ParagraphAlignment.LEFT);
        content.setTextPosition(20);
        for(WCode wCode: wCodes){
            content.setText(StringUtils.join(wCode.getCodes().toArray(), "") + "        ");
        }
    }

    @Override
    public List<ExportPatternEnum> getSupportedExportPatterns() {
        return Arrays.asList(ExportPatternEnum.WCODE_4D);
    }
}
