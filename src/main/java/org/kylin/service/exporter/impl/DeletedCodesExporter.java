package org.kylin.service.exporter.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
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

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
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

        if(ExportPatternEnum.getById(data.getExportFormat()).get() == ExportPatternEnum.DELETED_HAS_SAME_BIT_CODES){

            if(CollectionUtils.size(wCodesArray) < 3){
                return;
            }

            // 转换为3码非对子
            List<List<WCode>> threeDimCodesArray = wCodesArray.stream()
                    .map(wCodes -> WCodeUtils.filterNonPairCodes(wCodes.getData()))
                    .map(wCodes -> WCodeUtils.transferToPermutationThreeCodes(wCodes))
                    .collect(Collectors.toList());


            List<WCode> wCodes = getRepeatCheckedCodes(threeDimCodesArray);
            if(CollectionUtils.isEmpty(wCodes)){
                return ;
            }
            String subTitle = "累计杀码非对子 " + wCodes.size() + " 注: ";
            printCodes(docHolder.getDocument().createParagraph(), LabelValue.<List<WCode>>builder().data(wCodes).label("累计杀码集合").build(), subTitle);
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
            String subTitle = "A" + i+1 + "(操作:" + labelValue.getLabel() + ", 杀码非对子" + nonPairCodes.size() + " 注): ";
            printCodes(docHolder.getDocument().createParagraph(), nonPairLabelCodes, subTitle);

        }
    }

    @Deprecated
    private void printCodes(XWPFParagraph paragraph, LabelValue<List<WCode>> labelValue, String subTitle){

        Objects.requireNonNull(labelValue);

        if(CollectionUtils.isEmpty(labelValue.getData())){
            return;
        }

        List<WCode> wCodes = labelValue.getData();

        DocUtils.writeSubTitle(paragraph, subTitle);

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

    private List<WCode> getRepeatCheckedCodes(List<List<WCode>> wCodesArray) {
        if(CollectionUtils.isEmpty(wCodesArray)){
            return Collections.emptyList();
        }

        Map<WCode, Integer> codeStat = new HashMap<>();
        Set<Pair<Integer, Integer>> pairs = new HashSet<>();
        int count = 0;
        for(int i=0; i<wCodesArray.size(); i++){
            List<WCode> wCodes1 = wCodesArray.get(i);
            count += wCodes1.size();
            for(int j=0; j!=i && j<wCodesArray.size(); j++){
                Pair<Integer, Integer> pair = Pair.of(i, j);
                if(pairs.contains(pair)){
                    continue;
                }
                pairs.add(pair);
                pairs.add(Pair.of(j,i));

                log.info("计算pair：left={}, right={}", i, j);

                List<WCode> wCodesX = wCodesArray.get(j);
                Set<WCode> codes = getHasRepeatedCodesInLeftThreeBits(wCodes1, wCodesX);
                putAllRepeatedCodes(codeStat, codes);
            }
        }


        List<WCode> wCodeList = codeStat.entrySet().stream().filter(entry -> entry.getValue() >= 3).map(Map.Entry::getKey).collect(Collectors.toList());
        Collections.sort(wCodeList);
        log.info("合并取重完成 from: {} reduce to {} .", count, wCodeList.size());
        return wCodeList;
    }

    private void putAllRepeatedCodes(Map<WCode, Integer> wCodeStat, Set<WCode> wCodes){
        Objects.requireNonNull(wCodes);
        if(CollectionUtils.isEmpty(wCodes)){
            return;
        }

        for(WCode wCode : wCodes){
            wCodeStat.put(wCode, wCodeStat.getOrDefault(wCode, 0) + 1);
        }
    }


    private Set<WCode> getHasRepeatedCodesInLeftThreeBits(List<WCode> wCodes1,List<WCode> wCodes2){
        if(CollectionUtils.isEmpty(wCodes1) || CollectionUtils.isEmpty(wCodes2)){
            return Collections.emptySet();
        }

        Set<WCode> wCodes = new HashSet<>();
        for(int i=0; i<wCodes1.size(); i++){
            for(int j=0; j<wCodes2.size(); j++) {
                if(Objects.equals(wCodes1.get(i), wCodes2.get(j))){
                    wCodes.add(wCodes1.get(i));
                    wCodes.add(wCodes2.get(j));
                }
            }
        }

        return wCodes;
    }


    @Override
    public List<ExportPatternEnum> getSupportedExportPatterns() {
        return Arrays.asList(ExportPatternEnum.DELETED_CODES, ExportPatternEnum.DELETED_HAS_SAME_BIT_CODES);
    }
}
