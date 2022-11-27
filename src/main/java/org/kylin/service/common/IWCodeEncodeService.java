package org.kylin.service.common;

import org.kylin.bean.p5.WCode;

import java.util.List;
import java.util.Set;

/**
 * WCode编码工具类
 */
public interface IWCodeEncodeService {

    /**
     * 组合编码3d
     *
     * @param riddles
     * @return
     */
    List<WCode> compositionEncode(List<Set<Integer>> riddles);

}
