package org.kylin.algorithm.lock;

import org.kylin.exception.DistributedLockException;

/**
 * 分布式锁接口
 */
public interface DistributedLock {

    /**
     * 获取分布式锁
     *
     * @param key
     * @param timeoutMilli
     * @param value
     * @throws DistributedLockException
     */
    String tryLock(String key, Long timeoutMilli, String value) throws DistributedLockException;

    /**
     * 释放分布式锁
     *
     * @param key
     * @param value
     * @throws DistributedLockException
     */
    void releaseLock(String key, String value) throws DistributedLockException;


    /**
     * 指示实例是否可用
     *
     * @return
     */
    boolean isValid();

}
