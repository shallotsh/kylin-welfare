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

    /**
     * 二码法组码，直选组码
     *
     * @param riddles
     * @return
     */
    List<WCode> compositionWithQuibinaryEncode(List<Set<Integer>> riddles);


    /**
     * 复式组选组码
     *
     * @param riddle 一组数字
     * @param dimVal
     * @return 组码
     */
    List<WCode> combine4Code(List<Integer> riddle, int dimVal);


    /**
     * 使用二码和字典进行组码
     * @param riddle
     * @return
     */
    List<WCode> combineUsingDict(List<Integer> riddle);

}
