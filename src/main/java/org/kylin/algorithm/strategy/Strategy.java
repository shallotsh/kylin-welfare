package org.kylin.algorithm.strategy;


import java.util.Objects;

/**
 * @author shallotsh
 * @date 2017/7/29 下午2:14.
 */
@FunctionalInterface
public interface Strategy<T,P> {
    default boolean shouldExecute(P param){
        if(Objects.isNull(param)){
            return false;
        }
        return true;
    }
    T execute(P param, T source);
}
