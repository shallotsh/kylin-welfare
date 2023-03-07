package org.kylin.algorithm.strategy.impl;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.kylin.algorithm.strategy.SequenceProcessor;
import org.kylin.bean.p5.WCode;
import org.kylin.bean.p5.WCodeReq;
import org.kylin.util.TransferUtil;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 坐标杀
 * 描述：输入“n bcdefg m”，支持三组数输入
 *  n：表示第列序，即第几列
 *  bcdefg: 表示万位为“bcdefg”的数字
 *  m: 表示每行按m列计算，如果不输入m或者m输入错误（m需要大于5），则默认m=18
 * 生成规则：按m进行分组成矩阵，然后基于列序，循环匹配bcdefg，每位匹配码随机取5行的P5码，再将每行选择的P5码往后选取5个作为结果返回
 *
 * v1.1: 请设计坐标杀为可反复进行，每次杀都以第一次杀前的库为基础，而不是杀后剩下的库
 *
 */
@Slf4j
public class CoordKillProcessor implements SequenceProcessor {

    /**
     * 列序
     */
    private Integer rowIndex;

    /**
     * 万位匹配码
     */
    private Set<Integer> matchCodes;
    private Integer rowSize = 18;
    private List<WCode> wCodes;

    @Override
    public SequenceProcessor init(WCodeReq wCodeReq) {
        if(StringUtils.isNotBlank(wCodeReq.getBoldCodeFive())){
            String[] params = wCodeReq.getBoldCodeFive().split("#|$|@|,|/| ");
            if(params == null || params.length < 2){
                log.info("坐标杀，输入参数错误 seq:{}", wCodeReq.getBoldCodeFive());
                return this;
            }
            rowIndex = NumberUtils.toInt(params[0], -1);
            // 换算到Java列表索引
            if(rowIndex > 0){
                rowIndex -= 1;
            }
            matchCodes = TransferUtil.toIntegerSet(params[1]);

            if(params.length>2){
                Integer tmp = NumberUtils.toInt(params[2], -1);
                if(tmp > 5 && tmp < 20){
                    rowSize = tmp;
                }
            }

            log.info("rowIndex:{}, rowSize:{}, matchCodes:{}", rowIndex, rowSize, matchCodes);
        }

        wCodes = wCodeReq.getwCodes();

        return this;
    }

    @Override
    public List<WCode> process(List<WCode> deletedCodes) {
        if(!validate()){
            return wCodes;
        }

        List<WCode> ret = new ArrayList<>();

        List<List<WCode>> codesMatrix = Lists.partition(wCodes, rowSize);
        for(Integer matchCode : matchCodes){
            ret.addAll(findP5CodeByMatching(rowIndex, matchCode, codesMatrix));
        }
        return ret;
    }


    private List<WCode> findP5CodeByMatching(int rowIndexValue, int matchCode, List<List<WCode>> matrix){

        List<Integer> matchedRowIds = findMatchedRowIds(rowIndexValue, matchCode, matrix);
        if(CollectionUtils.isEmpty(matchedRowIds)){
            log.info("matchCode未匹配 rowIndexValue:{}, matchCode:{}", rowIndexValue, matchCode);
            return Collections.emptyList();
        }
        List<WCode> wCodeList = new ArrayList<>();
        for(Integer matchedRowId : matchedRowIds){
            List<WCode> originCodes = matrix.get(matchedRowId);
            for(int i=rowIndexValue; i<(rowIndexValue+5) && i<originCodes.size(); i++){
                wCodeList.add(originCodes.get(i));
            }
        }
        return wCodeList;
    }

    private List<Integer> findMatchedRowIds(int rowIndexValue, int matchCode, List<List<WCode>> matrix){
        List<Integer> matchedRowIds = new ArrayList<>();
        for(int index = 0 ; index < matrix.size(); index++){
            List<WCode> wCodeList = matrix.get(index);
            if(rowIndexValue >= wCodeList.size()){
                log.info("列序大于分组大小 rowIndexValue:{}, size:{}", rowIndexValue, wCodeList.size());
                continue;
            }
            WCode wCode = wCodeList.get(rowIndexValue);
            if(Objects.equals(wCode.getCodes().get(0), matchCode)){
                matchedRowIds.add(index);
            }
        }

        List<Integer> ret = new ArrayList<>();
        // 如果结果大于5，则随机取5行
        if(matchedRowIds.size() > 5){
            int maxCnt = 10000;
            int tryCnt = 0;
            while(true && ++tryCnt < maxCnt) {
                int randomValue = new Random().nextInt(matchedRowIds.size()) ;
                if (ret.contains(matchedRowIds.get(randomValue))) {
                    continue;
                }
                ret.add(matchedRowIds.get(randomValue));
                if(ret.size() >= 5){
                    break;
                }
            }
            log.info("随机处理命中行ID rowIndex:{}, matchCode:{}, ret:{}, matchedRowIds:{}", rowIndexValue, matchCode, ret, matchedRowIds);
            return ret;
        }else{
            log.info("返回命中行ID rowIndex:{}, matchCode:{} matchedRowIds:{}", rowIndexValue, matchCode, matchedRowIds);
            return matchedRowIds;
        }
    }



    @Override
    public boolean validate() {
        if(CollectionUtils.isEmpty(wCodes) ){
            return false;
        }

        if(rowIndex == null
                ||  Objects.equals(rowIndex, -1)
                || CollectionUtils.isEmpty(matchCodes)
                || rowIndex >= rowSize){
            log.info("坐标杀 输入不合法 rowIndex:{}, rowSize:{}, matchCodes:{}", rowIndex, rowSize, matchCodes);
            return false;
        }

        return true;
    }
}
