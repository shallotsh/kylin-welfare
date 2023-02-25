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
import org.kylin.util.WCodeUtils;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 四码转三键导出，按特定格式转换输出
 */
@Component
public class W4DTo3DDocExporter extends AbstractDocumentExporter{

    @Override
    public void writeContentToDoc(DocHolder docHolder, WCodeReq data) {
        Objects.requireNonNull(docHolder);
        Objects.requireNonNull(data);

        if(CollectionUtils.isEmpty(data.getWCodes())){
            return;
        }

        List<WCode> wCodes = data.getWCodes();
//        Collections.sort(wCodes);

        // 导出四码分解
        List<WCode> fourCodeDecomposes = getDecomposeCodes(wCodes);
        String title = String.format("四码分解 %d 注:",fourCodeDecomposes.size());
        exportCodes(docHolder.getDocument().createParagraph(),  fourCodeDecomposes, title);

        // 导出aabc
        // 对子
        List<WCode> pairCodes = getAABCCodesForPair(wCodes);
        title = String.format("对子（%d 注）:", pairCodes.size());
        exportCodes(docHolder.getDocument().createParagraph(), pairCodes, title);

        // 非对子
        List<WCode> nonPairCodes = getAABCCodesForNonPair(wCodes);
        title = String.format("非对子（%d 注）:", nonPairCodes.size());
        exportCodes(docHolder.getDocument().createParagraph(), nonPairCodes, title);

    }


    private List<WCode> getAABCCodesForPair(List<WCode> wCodes){
        return generateGroupCodes(wCodes, this::isAABC, this::buildPairCodesForAABC);
    }

    private List<WCode> getAABCCodesForNonPair(List<WCode> wCodes){
        return generateGroupCodes(wCodes, this::isAABC, this::buildNonPairCodesForAABC);
    }

    private List<WCode> generateGroupCodes(List<WCode> wCodes, Predicate<List<Integer>> judge, Function<List<Integer>,List<WCode>> func){
        if(CollectionUtils.isEmpty(wCodes)){
            return Collections.emptyList();
        }
        List<WCode> ret = new ArrayList<>();
        for(WCode wCode : wCodes){
            if(!judge.test(wCode.getCodes())){
                continue;
            }
            ret.addAll(func.apply(wCode.getCodes()));
        }

        return WCodeUtils.convertToGroup(ret);
    }

    private List<WCode> buildPairCodesForAABC(List<Integer> codes){
        List<WCode> wCodes = new ArrayList<>();
        Map<Integer, Long> codeToCount = codes.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        List<Map.Entry<Integer, Long>> entries = codeToCount.entrySet().stream().collect(Collectors.toList());
        Collections.sort(entries, Map.Entry.comparingByValue());

        wCodes.add(new WCode(3, entries.get(2).getKey(), entries.get(2).getKey(), entries.get(0).getKey()));
        wCodes.add(new WCode(3, entries.get(2).getKey(), entries.get(2).getKey(), entries.get(1).getKey()));
        wCodes.add(new WCode(3, codes.get(0), codes.get(0), codes.get(3)));
        return wCodes;
    }

    private List<WCode> buildNonPairCodesForAABC(List<Integer> codes){
        List<WCode> wCodes = new ArrayList<>();
        Map<Integer, Long> codeToCount = codes.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        List<Map.Entry<Integer, Long>> entries = codeToCount.entrySet().stream().collect(Collectors.toList());
        Collections.sort(entries, Map.Entry.comparingByValue());
        wCodes.add(new WCode(3, entries.get(2).getKey(), entries.get(1).getKey(), entries.get(0).getKey()));
        return wCodes;
    }

    private boolean isAABC(List<Integer> codes){
        if(CollectionUtils.isEmpty(codes) || codes.size() != 4){
            return false;
        }
        return new HashSet<>(codes).size() == 3;
    }


    private List<WCode> getDecomposeCodes(List<WCode> wCodes){
        if(CollectionUtils.isEmpty(wCodes)){
            return Collections.emptyList();
        }

        return generateGroupCodes(wCodes, this::isAllDiff, this::decompose);
    }

    private boolean isAllDiff(List<Integer> codes){
        if(CollectionUtils.isEmpty(codes) || codes.size() != 4){
            return false;
        }
        return new HashSet<>(codes).size() == 4;
    }

    private List<WCode> decompose(List<Integer> codes){
        List<WCode> ret = new ArrayList<>();
        ret.add(new WCode(3, codes.get(0), codes.get(1), codes.get(2)));
        ret.add(new WCode(3, codes.get(1), codes.get(2), codes.get(3)));
        return ret;
    }

    private void exportCodes(XWPFParagraph paragraph, List<WCode> wCodes, String title){

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
        return Arrays.asList(ExportPatternEnum.WCODE_4D_TO_3D);
    }
}
