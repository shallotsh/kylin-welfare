package org.kylin.application;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.kylin.bean.p4.W3DCompoundCodeReq;
import org.kylin.bean.p5.WCode;
import org.kylin.constant.EW4DClassify;
import org.kylin.constant.ExportPatternEnum;
import org.kylin.service.common.IWCodeEncodeService;
import org.kylin.service.exporter.DocHolder;
import org.kylin.service.exporter.ExportToolSelector;
import org.kylin.service.exporter.IDocExportTool;
import org.kylin.service.xcode.filters.impl.BoldCodeFilter;
import org.kylin.service.xcode.filters.impl.LateAutumnCodeFilter;
import org.kylin.service.xcode.filters.impl.SumTailCodeFilter;
import org.kylin.util.ExporterControlUtil;
import org.kylin.util.TransferUtil;
import org.kylin.util.WCodeUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@Slf4j
public class W4DApplicationService {

    @Resource
    private IWCodeEncodeService iwCodeEncodeService;

    @Resource
    private ExportToolSelector exportToolSelector;

    public List<WCode> doComposition(List<String> riddles){
        if(CollectionUtils.isEmpty(riddles) || CollectionUtils.size(riddles) != 1 ){
            return Collections.emptyList();
        }
        List<Integer> seq = TransferUtil.toIntegerList(riddles.get(0));
        if(CollectionUtils.isEmpty(seq)){
            log.info("预测序列不合法 riddles:{}", riddles);
            throw new RuntimeException("预测序列为空");
        }
        List<WCode> wCodes = iwCodeEncodeService.combine4Code(seq, 4);
        // abcd中有一对重复的也留下，即aabc型留下，其余aabb型（a不等于b），aaab，aaaa型都去除
        wCodes = wCodes.stream().filter(x -> WCodeUtils.meetConditionFor4D(x)).collect(Collectors.toList());

        log.info("组码结果 seq:{}, wCodes_size:{}", seq, CollectionUtils.size(wCodes));
        return wCodes;
    }

    public List<WCode> doKill(W3DCompoundCodeReq req){

        if(Objects.isNull(req)){
            return Collections.emptyList();
        }

        List<WCode> target = req.getWCodes();
        int count = CollectionUtils.size(target);
        log.info("4d杀码前 {} 注3D", count);

        if(CollectionUtils.isNotEmpty(target) && StringUtils.isNotBlank(req.getBoldCodeSeq())){
            target  = new BoldCodeFilter().filter(target, req.getBoldCodeSeq());
            log.info("胆杀 {} 注3D", (count - CollectionUtils.size(target)));
            count = CollectionUtils.size(target);
        }


        if(CollectionUtils.isNotEmpty(target) && StringUtils.isNotBlank(req.getSumTailValues())){
            target = new SumTailCodeFilter().filter(target, req.getSumTailValues());
            log.info("三和尾杀 {} 住3D", (count - CollectionUtils.size(target)));
            count = CollectionUtils.size(target);
        }


        /**
         * 临时方案：需要每次放最后杀码，确保晚秋选码后不会丢失软删除码
         */
        if(CollectionUtils.isNotEmpty(target) && StringUtils.isNotBlank(req.getLateAutumnCode())){
            target = new LateAutumnCodeFilter().filter(target, req.getLateAutumnCode());
            log.info("晚秋选码杀 {} 住3D", target.stream().filter(w -> w.isDeleted()).count());
            count = CollectionUtils.size(target);
        }

//        if(CollectionUtils.isNotEmpty(target)
//                && StringUtils.isNotBlank(req.getKdSeq())){
//            target = new KdSimpleFilter().filter(target, req.getKdSeq());
//            log.info("跨度杀 {} 注3D", (count - CollectionUtils.size(target)));
//            count = CollectionUtils.size(target);
//        }

//        if(CollectionUtils.isNotEmpty(target)
//                && StringUtils.isNotBlank(req.getBinSumValues())){
//            target = new BinSumFilter().filter(target, req.getBinSumValues());
//            log.info("二和杀 {} 注3D", (count - CollectionUtils.size(target)));
//            count = CollectionUtils.size(target);
//        }

        log.info("4d杀码后 {} 注3D", count);
        return target;
    }

    public List<WCode> transferToThreeCode(List<WCode> wCodes){
        if(CollectionUtils.isEmpty(wCodes)){
            return Collections.emptyList();
        }

        List<WCode> ret = new ArrayList<>();
        // 先取消四码分解
//        ret.addAll(getDecomposeCodes(wCodes));
        ret.addAll(getAABCCodesForPair(wCodes));
        ret.addAll(getAABCCodesForNonPair(wCodes));
        return ret;
    }

    private List<WCode> getDecomposeCodes(List<WCode> wCodes){
        if(CollectionUtils.isEmpty(wCodes)){
            return Collections.emptyList();
        }

        List<WCode> ret = generateGroupCodes(wCodes, this::isAllDiff, this::decompose);
        ret.stream().forEach(x -> x.setClassify(EW4DClassify.FOUR_DECOMPOSE.getId()));
        return ret;
    }

    private List<WCode> getAABCCodesForPair(List<WCode> wCodes){
        List<WCode> ret = generateGroupCodes(wCodes, this::isAABC, this::buildPairCodesForAABC);
        ret.forEach(x -> x.setClassify(EW4DClassify.FOUR_PAIR_CODE.getId()));
        return ret;
    }

    private List<WCode> getAABCCodesForNonPair(List<WCode> wCodes){
        List<WCode> ret = generateGroupCodes(wCodes, this::isAABC, this::buildNonPairCodesForAABC);
        ret.forEach(x -> x.setClassify(EW4DClassify.FOUR_NON_PAIR_CODE.getId()));
        return ret;
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






    public Optional<String> exportCodeToFile(W3DCompoundCodeReq req) throws IOException {

        try {
            ExportPatternEnum exportPatternEnum = ExporterControlUtil.getPatternType();
            if(exportPatternEnum == null){
                log.info("导出模式获取为空，导出失败 req:{}", JSON.toJSONString(req));
                return Optional.empty();
            }
            // 策略导出
            DocHolder docHolder = new DocHolder();
            Optional<IDocExportTool> iDocExportTool = exportToolSelector.getByExportPattern(exportPatternEnum);

            if (iDocExportTool.isPresent()) {
                iDocExportTool.get().writeTitleAsDefaultFormat(docHolder, "我要发·" + exportPatternEnum.getDesc());
                iDocExportTool.get().writeContentToDoc(docHolder, req.adaptToWCodeReq());
                String fileName = iDocExportTool.get().exportDocAsFile(docHolder);
                return Optional.of(fileName);
            } else {
                return Optional.empty();
            }
        } finally {
            ExporterControlUtil.clearPatternType();
        }
    }
}
