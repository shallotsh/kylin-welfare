package org.kylin.service.p3.impl;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.kylin.bean.p3.TwoDeriveThreeReq;
import org.kylin.bean.p5.WCode;
import org.kylin.constant.ExportPatternEnum;
import org.kylin.service.exporter.DocHolder;
import org.kylin.service.exporter.ExportToolSelector;
import org.kylin.service.exporter.IDocExportTool;
import org.kylin.service.p3.TwoDeriveThreeCodeService;
import org.kylin.service.xcode.filters.impl.*;
import org.kylin.util.WCodeUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;

@Service
@Slf4j
public class TwoDeriveThreeCodeServiceImpl implements TwoDeriveThreeCodeService {

    @Resource
    private ExportToolSelector exportToolSelector;


    @Override
    public List<WCode> shuffleCodes(List<List<Integer>> riddleArray) {

        List<WCode> twoCodes = compose2DCodes(riddleArray);
        if(CollectionUtils.isEmpty(twoCodes)){
            return Collections.emptyList();
        }
        List<WCode> threeCodes = compose3DCodes(twoCodes);

        log.info("size of twoCodes = {}, threeCodes = {}", twoCodes.size(), threeCodes.size());
        log.info("shuffleCodes result-> twoCodes = {}", JSON.toJSONString(twoCodes));
        log.info("shuffleCodes result-> threeCodes = {}", JSON.toJSONString(threeCodes));

        return threeCodes;
    }

    /**
     * 组2D码，返回List<WCode>
     *      组2D码的步骤：
     *          1. 先对输入的每个 List<Integer> 对象组码，每两两个元素组成一个码。
     *            比如输入的是[[1,2,3],[1,2,6]]，则组码结果为：
     *              [1,2,3] -> 结果：12,13,23
     *               [1,2,6] -> 结果：12,16,26
     *          2.再对每组2D码进去重合并，返回List<WCode>。
     *              [12,13,23],[12,16,26] -> 结果：12,13,16,23,26
     *              每个结果转换为WCode对象，并返回List<WCode>
     * @param riddleArray
     * @return
     */
    private List<WCode> compose2DCodes(List<List<Integer>> riddleArray){
        if(CollectionUtils.isEmpty(riddleArray)){
            return Collections.emptyList();
        }
        List<WCode> wCodes = new ArrayList<>();
        for(List<Integer> riddle : riddleArray) {
            for (int i = 0; i < riddle.size() - 1; i++) {
                for (int j = i + 1; j < riddle.size(); j++) {
                    wCodes.add(new WCode(2,riddle.get(i) , riddle.get(j)));
                }
            }
        }
        // 按组选去重
        return WCodeUtils.convertToGroup(wCodes);
    }


    /**
     * 组3D码，返回List<WCode>
     *      组3D码的步骤：
     *          1. 先对输入的每个 WCode 对象组码，两两比较组码。
     *            比如输入的是[12,13,16,23,26]，则组码结果为：
     *              [12,13,16,23,26] -> 结果：123，126，123,126,136,132
     *
     *          2.再对每组3D码进去重合并，返回List<WCode>。
     *              [123，126，123,126,136,132] -> 结果：123，126，136，132
     *              每个结果转换为WCode对象，并返回List<WCode>
     * @param wCodes
     * @return
     */
    private List<WCode> compose3DCodes(List<WCode> wCodes){

        if(CollectionUtils.isEmpty(wCodes)){
            return Collections.emptyList();
        }
        // 根据注释生成代码
        List<WCode> threeCodes = new ArrayList<>();
        for(int i=0; i<wCodes.size()-1; i++) {
            for (int j = i + 1; j < wCodes.size(); j++) {
                WCode wCode1 = wCodes.get(i);
                WCode wCode2 = wCodes.get(j);
                int sameCode = findOneSameCode(wCode1, wCode2);
                if(sameCode != -1) {
                    List<Integer> ret = new ArrayList<>();
                    ret.addAll(wCode1.getCodes());
                    int value = -1;
                    for(int k=0; k<wCode2.getCodes().size(); k++){
                        if(wCode2.getCodes().get(k) != sameCode){
                            value = wCode2.getCodes().get(k);
                            break;
                        }
                    }
                    if(value == -1){
                        value = wCode2.getCodes().get(0);
                    }
                    ret.add(value);
                    log.info("compose3DCodes {}, {} -> tmp:{}", wCode1.getString(false), wCode2.getString(false), ret);
                    threeCodes.add(new WCode(3, ret));
                }
            }
        }
        return WCodeUtils.convertToGroup(threeCodes);
    }

    private Integer findOneSameCode(WCode wCode1, WCode wCode2){
        for(Integer code : wCode1.getCodes()){
            if(wCode2.getCodes().contains(code)){
                return code;
            }
        }
        return -1;
    }

    @Override
    public List<WCode> convertToDirectCodes(List<WCode> wCodes) {

        if (CollectionUtils.isEmpty(wCodes)) {
            return Collections.emptyList();
        }
        log.info("convertToDirectCodes input(size{})-> {}", wCodes.size(), JSON.toJSONString(wCodes));
        List<WCode> directCodes = WCodeUtils.combine3DCode(wCodes);
        log.info("convertToDirectCodes output(size{})-> {}", directCodes.size(), JSON.toJSONString(directCodes));
        return directCodes;
    }
    @Override
    public List<WCode> killCode(TwoDeriveThreeReq req) {
        if (Objects.isNull(req)) {
            return Collections.emptyList();
        }

        List<WCode> target = req.getWCodes();
        int count = CollectionUtils.size(target);
        log.info("杀码前 {} 注3D", count);

        if (CollectionUtils.isNotEmpty(target) && StringUtils.isNotBlank(req.getBoldCodeSeq())) {
            target = new BoldCodeFilter().filter(target, req.getBoldCodeSeq());
            log.info("胆杀 {} 注3D", (count - CollectionUtils.size(target)));
            count = CollectionUtils.size(target);
        }

        if (CollectionUtils.isNotEmpty(target) && StringUtils.isNotBlank(req.getSumTailValues())) {
            target = new SumTailCodeFilter().filter(target, req.getSumTailValues());
            log.info("和尾杀 {} 住3D", (count - CollectionUtils.size(target)));
            count = CollectionUtils.size(target);
        }


        if (CollectionUtils.isNotEmpty(target)
                && StringUtils.isNotBlank(req.getKdSeq())) {
            target = new KdSimpleFilter().filter(target, req.getKdSeq());
            log.info("跨度杀 {} 注3D", (count - CollectionUtils.size(target)));
            count = CollectionUtils.size(target);
        }

        if (CollectionUtils.isNotEmpty(target) && StringUtils.isNotBlank(req.getSeqKill())) {
            target = new StrictSeqKillFilter().filter(target, req.getSeqKill());
            log.info("严格顺序杀 {} 注3D", (count - CollectionUtils.size(target)));
            count = CollectionUtils.size(target);
        }

        log.info("杀码后 {} 注3D", count);
        return target;
    }

    @Override
    public Optional<String> exportCodeToFile(TwoDeriveThreeReq req) throws IOException {
        // 策略导出
        DocHolder docHolder = new DocHolder();
        Optional<IDocExportTool> iDocExportTool = exportToolSelector.getByExportPattern(ExportPatternEnum.TWO_DERIVE_THREE_CODE);

        if(iDocExportTool.isPresent()) {
            iDocExportTool.get().writeTitleAsDefaultFormat(docHolder, ExportPatternEnum.TWO_DERIVE_THREE_CODE.getDesc());
            iDocExportTool.get().writeContentToDoc(docHolder, req.adaptToWCodeReq());
            String fileName = iDocExportTool.get().exportDocAsFile(docHolder);
            return Optional.of(fileName);
        }else{
            return Optional.empty();
        }
    }
}
