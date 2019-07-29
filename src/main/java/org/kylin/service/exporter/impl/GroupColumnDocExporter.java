package org.kylin.service.exporter.impl;

import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.kylin.bean.p5.WCode;
import org.kylin.bean.p5.WCodeReq;
import org.kylin.constant.ExportPatternEnum;
import org.kylin.service.exporter.AbstractDocumentExporter;
import org.kylin.util.CommonUtils;
import org.kylin.util.DocUtils;
import org.kylin.util.WCodeUtils;

import java.util.*;

public class GroupColumnDocExporter extends AbstractDocumentExporter<WCodeReq> {

    private Set<Integer> columnIdxs = new HashSet<>(Arrays.asList(0,1,2));
    private int groupSize = 13;
    private List<WCode> wCodes;

    public GroupColumnDocExporter(XWPFDocument doc, WCodeReq data) {
        super(doc, data);
    }

    @Override
    public void init() {
        super.init();
        List<WCode> oriCodes = data.getWCodes();
        if(CollectionUtils.isEmpty(oriCodes)){
            wCodes = Collections.emptyList();
            return;
        }
        wCodes = new ArrayList<>();
        List<List<WCode>> codeArray = Lists.partition(oriCodes, 13);
        for(List<WCode> wCodeList : codeArray){
            for(int i=0; i<wCodeList.size(); i++){
                if(columnIdxs.contains(i)){
                    wCodes.add(wCodeList.get(i));
                }

            }
        }
    }

    @Override
    public void writeStats() {
        if(CollectionUtils.isEmpty(wCodes)){
            return;
        }

        XWPFParagraph header = doc.createParagraph();
        XWPFRun hr2 = header.createRun();
        Optional<ExportPatternEnum> ep = ExportPatternEnum.getById(data.getExportFormat());
        if(!ep.isPresent()){
            return;
        }
        hr2.setText(toUTF8(ep.get().getDesc() + " 共计" + CollectionUtils.size(wCodes)+ "注排列5码!!!     时间："
                + CommonUtils.getCurrentDateString() ));
        hr2.setTextPosition(10);
        hr2.setFontSize(18);

        XWPFRun hr3 = header.createRun();
        hr3.setText(" ");
        hr3.addBreak();
    }

    @Override
    public void writeBody() {
        if(CollectionUtils.isEmpty(wCodes)){
            return;
        }

//        Collections.sort(wCodes);

        Optional<ExportPatternEnum> ep = ExportPatternEnum.getById(data.getExportFormat());
        if(!ep.isPresent()){
            return;
        }

        List<WCode> pairCodes = WCodeUtils.filterPairCodes(wCodes);
        List<WCode> nonPairCodes = WCodeUtils.filterNonPairCodes(wCodes);


        if(!CollectionUtils.isEmpty(pairCodes)) {
            String titleString = String.format(ep.get().getDesc() + " 对子( %d 注)", pairCodes.size());
            DocUtils.exportWCodes(doc, pairCodes, titleString, null, data.getFreqSeted(), null, ep.get() == ExportPatternEnum.NORMAL_SEQ_NO ? true: false);
        }

        if(!CollectionUtils.isEmpty(nonPairCodes)) {
            String titleString = String.format(ep.get().getDesc() + " 非对子( %d 注)", nonPairCodes.size());
            DocUtils.exportWCodes(doc, nonPairCodes, titleString, null, data.getFreqSeted(), null, ep.get() == ExportPatternEnum.NORMAL_SEQ_NO ? true: false);
        }

    }
}
