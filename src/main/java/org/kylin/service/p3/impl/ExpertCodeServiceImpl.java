package org.kylin.service.p3.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.kylin.bean.p3.ExpertCodeReq;
import org.kylin.bean.p5.WCode;
import org.kylin.constant.ExportPatternEnum;
import org.kylin.service.exporter.DocHolder;
import org.kylin.service.exporter.ExportToolSelector;
import org.kylin.service.exporter.IDocExportTool;
import org.kylin.service.p3.ExpertCodeService;
import org.kylin.service.xcode.filters.impl.*;
import org.kylin.util.WCodeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ExpertCodeServiceImpl implements ExpertCodeService {

    @Resource
    private ExportToolSelector exportToolSelector;

    @Override
    public List<WCode> expertEncode(List<Integer> riddleSeq) {
        if(CollectionUtils.size(riddleSeq) < 3){
            return Collections.emptyList();
        }

        // 编码
        Set<WCode> wCodes = new HashSet<>();
        for(int i=0; i<riddleSeq.size(); i++){
            for(int j=i+1; j<riddleSeq.size(); j++){
                for(int k=j+1; k<riddleSeq.size(); k++){
                    if(i != j && i != k && j != k){
                        WCode wCode = new WCode(3, riddleSeq.get(i), riddleSeq.get(j), riddleSeq.get(k));
                        wCodes.add(wCode);
                    }
                }
            }
        }

        List<WCode> ret = new ArrayList<>(wCodes);
        Collections.sort(ret);

        return ret;
    }


    @Override
    public List<WCode> convertTo2DCodesForEveryFreq(List<WCode> wCodes) {
        if(CollectionUtils.isEmpty(wCodes)){
            return Collections.emptyList();
        }
        int count = CollectionUtils.size(wCodes);

        Map<Integer, List<WCode>> freqToCodes = wCodes.stream().collect(Collectors.groupingBy(WCode::getFreq));

        List<WCode> target = new ArrayList<>();
        // 按频次转组选
        freqToCodes.forEach((freq, codes) -> target.addAll(WCodeUtils.convert3DTo2D(codes)));

        Collections.sort(target);
        log.info("按频次转2D：余 {} 注3D, 减少 {} 注 3D", target.size(), (count - target.size()));
        return target;
    }

    @Override
    public List<WCode> convertToGroupCodesForEveryFreq(List<WCode> wCodes) {
        if(CollectionUtils.isEmpty(wCodes)){
            return Collections.emptyList();
        }
        int count = CollectionUtils.size(wCodes);
        Map<Integer, List<WCode>> freqToCodes = wCodes.stream().collect(Collectors.groupingBy(WCode::getFreq));

        List<WCode> target = new ArrayList<>();
        // 按频次转组选
        freqToCodes.forEach((freq, codes) -> target.addAll(WCodeUtils.convertToGroup(codes)));

        Collections.sort(target);
        log.info("按频次转组选：余 {} 注3D, 减少 {} 注 3D", target.size(), (count - target.size()));
        return target;
    }

    @Override
    public List<WCode> killCode(ExpertCodeReq req) {

        if(Objects.isNull(req)){
            return Collections.emptyList();
        }

        List<WCode> target = req.getWCodes();
        int count = CollectionUtils.size(target);
        log.info("杀码前 {} 注3D", count);

        if(CollectionUtils.isNotEmpty(target) && StringUtils.isNotBlank(req.getBoldCodeSeq())){
            target  = new BoldCodeFilter().filter(target, req.getBoldCodeSeq());
            log.info("胆杀 {} 注3D", (count - CollectionUtils.size(target)));
            count = CollectionUtils.size(target);
        }

        if(CollectionUtils.isNotEmpty(target) && StringUtils.isNotBlank(req.getInverseCodeSeq())){
            target = new InverseSelectCodeFilter().filter(target, req.getInverseCodeSeq());
            log.info("筛选杀 {} 注3D", (count - CollectionUtils.size(target)));
            count = CollectionUtils.size(target);
        }

        if(CollectionUtils.isNotEmpty(target) && StringUtils.isNotBlank(req.getGossipCodeSeq())){
            target = new GossipSimpleFilterr().filter(target, req.getGossipCodeSeq());
            log.info("二码杀 {} 注3D", (count - CollectionUtils.size(target)));
            count = CollectionUtils.size(target);
        }

        if(CollectionUtils.isNotEmpty(target) && StringUtils.isNotBlank(req.getSumTailValues())){
            target = new SumTailCodeFilter().filter(target, req.getSumTailValues());
            log.info("和尾杀 {} 住3D", (count - CollectionUtils.size(target)));
            count = CollectionUtils.size(target);
        }


        if(CollectionUtils.isNotEmpty(target)
                && StringUtils.isNotBlank(req.getKdSeq())){
            target = new KdSimpleFilter().filter(target, req.getKdSeq());
            log.info("跨度杀 {} 注3D", (count - CollectionUtils.size(target)));
            count = CollectionUtils.size(target);
        }

        if(CollectionUtils.isNotEmpty(target) && StringUtils.isNotBlank(req.getSeqKill())){
            target = new SeqKillFilter().filter(target, req.getSeqKill());
            log.info("顺序杀 {} 注3D", (count - CollectionUtils.size(target)));
            count = CollectionUtils.size(target);
        }

        if(CollectionUtils.isNotEmpty(target) && req.getBitUnitDTO() != null && req.getBitUnitDTO().isValid()){
            req.getBitUnitDTO().setNeedDeletedCodes(true);
            target = new BitUnitCompositeFilter().filter(target, req.getBitUnitDTO());
            log.info("位选 {} 注3D", (count - CollectionUtils.size(target)));
            count = CollectionUtils.size(target);
        }

        if(CollectionUtils.isNotEmpty(target) && StringUtils.isNotBlank(req.getTwoCodeSumSeq())){

            target = new TwoCodeSumFilter().filter(target, req.getTwoCodeSumSeq());
            log.info("二码和 {} 注3D", (count - CollectionUtils.size(target)));
            count = CollectionUtils.size(target);

        }


        log.info("杀码后 {} 注3D", count);
        return target;
    }

    @Override
    public Optional<String> exportCodeToFile(ExpertCodeReq req) throws IOException {
        // 策略导出
        DocHolder docHolder = new DocHolder();
        Optional<IDocExportTool> iDocExportTool = exportToolSelector.getByExportPattern(ExportPatternEnum.EXPERT_3D);

        if(iDocExportTool.isPresent()) {
            iDocExportTool.get().writeTitleAsDefaultFormat(docHolder, "我要发·专家推荐法");
            iDocExportTool.get().writeContentToDoc(docHolder, req.adaptToWCodeReq());
            String fileName = iDocExportTool.get().exportDocAsFile(docHolder);
            return Optional.of(fileName);
        }else{
            return Optional.empty();
        }
    }
}
