package org.kylin.service.common.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.kylin.bean.p5.WCode;
import org.kylin.config.BinCodeSumDictConfig;
import org.kylin.service.common.IWCodeEncodeService;
import org.kylin.util.WCodeUtils;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class WCodeEncodeServiceImpl implements IWCodeEncodeService {

    @Override
    public List<WCode> compositionEncode(List<Set<Integer>> riddles) {
        if(CollectionUtils.isEmpty(riddles) || riddles.size() != 3) {
            throw new RuntimeException("不支持的编码");
        }

        List<WCode> wCodes = new ArrayList<>();

        for(Integer e1: riddles.get(0)){
            for(Integer e2: riddles.get(1)){
                for(Integer e3: riddles.get(2)){
                    wCodes.add(new WCode(3, e1, e2, e3));
                }
            }
        }

        return WCodeUtils.convertToGroup(wCodes);
    }

    @Override
    public List<WCode> compositionWithQuibinaryEncode(List<Set<Integer>> riddles) {

        if(CollectionUtils.isEmpty(riddles) || riddles.size() <3 || riddles.size() > 4) {
            throw new RuntimeException("不支持的编码");
        }

        List<WCode> wCodes = new ArrayList<>();
        for(int i=0; i<riddles.size(); i++){
            for(int j=0; j<riddles.size(); j++){
                if(i == j){
                    continue;
                }
                wCodes.addAll(compositionWithQuibinary(riddles.get(i), riddles.get(j)));
            }
        }
        return WCodeUtils.convertToGroup(wCodes);
    }

    private List<WCode> compositionWithQuibinary(Set<Integer> set1, Set<Integer> set2){
        List<WCode> wCodes = new ArrayList<>();
        for(Integer e1: set1){
            for(Integer e2: set2 ){
                for (int k = 0; k <= 9; k++) {
                    wCodes.add(new WCode(3, e1, e2, k));
                }
            }
        }
        return wCodes;
    }

    @Override
    public List<WCode> combine4Code(List<Integer> riddle, int dimVal) {
        if(dimVal <= 0 || CollectionUtils.size(riddle) < dimVal){
            return Collections.emptyList();
        }
        boolean[] flag = new boolean[riddle.size()];
        List<List<Integer>> res = new ArrayList<>();
        combineRefactor(riddle, 0, dimVal, flag, res);

        List<WCode> wCodes = new ArrayList<>();
        for(List<Integer> code : res){
            wCodes.add(new WCode(dimVal, code));
        }
        return WCodeUtils.convertToGroup(wCodes);
    }

    private void combineRefactor(List<Integer> riddle, int start, int count, boolean[] flag, List<List<Integer>> res){

        if(count == 0){
            List<Integer> tmp = new ArrayList<>();
            for(int i=0; i<flag.length; i++){
                if(flag[i]){
                    tmp.add(riddle.get(i));
                }
            }
            res.add(tmp);
            return;
        }
        if(start == riddle.size()){
            return;
        }
        flag[start] = true;
        combineRefactor(riddle, start+1,  count - 1, flag, res);
        flag[start] = false;
        combineRefactor(riddle, start+1,  count, flag, res);
    }

    @Override
    public List<WCode> combineUsingDict(List<Integer> riddle) {
        return BinCodeSumDictConfig.getWCodesByBinCodes(riddle);
    }
}
