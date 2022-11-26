package org.kylin.service.origin3d.encode;

import org.kylin.bean.WelfareCode;

import java.util.List;
import java.util.Set;

/**
 * @author shallotsh
 * @date 2017/6/29 下午11:26.
 */
public interface WyfEncodeService {

    /**
     * 二码法编码
     * @param riddles
     * @return
     */
    WelfareCode quibinaryEncode(List<Set<Integer>> riddles);

    /**
     * 组选编码器
     * @param riddles
     * @return
     */
    WelfareCode groupSelectEncode(List<Set<Integer>> riddles);

    /**
     * 直选编码器
     * @param riddles
     * @return
     */
    WelfareCode directSelectEncode(List<Set<Integer>> riddles);
}
