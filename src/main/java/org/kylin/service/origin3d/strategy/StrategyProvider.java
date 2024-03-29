package org.kylin.service.origin3d.strategy;

import org.kylin.algorithm.strategy.Strategy;
import org.kylin.bean.WelfareCode;
import org.kylin.bean.WyfParam;

/**
 * @author shallotsh
 * @date 2017/7/29 下午2:08.
 */
public interface StrategyProvider {
    WelfareCode encode(WyfParam param, Strategy<? super WelfareCode, ? super WyfParam> strategy);
}
