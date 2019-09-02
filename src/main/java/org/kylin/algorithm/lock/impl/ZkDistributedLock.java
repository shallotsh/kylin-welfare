package org.kylin.algorithm.lock.impl;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.kylin.adapter.zk.ZooKeeperService;
import org.kylin.algorithm.lock.DistributedLock;
import org.kylin.bean.ConfigNode;
import org.kylin.exception.AppExceptionEnum;
import org.kylin.exception.DistributedLockException;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ZkDistributedLock implements DistributedLock {

    private static final String LOCK_BASE_PATH = "/locks";
    private static final String SEPARATOR = "_";

    private ThreadLocal<LockHolder> lockHolderThreadLocal  = new ThreadLocal<>();

    @Getter
    private boolean isInitSuccess = false;

    @Resource
    private ZooKeeperService zooKeeperService;

    @PostConstruct
    public void init(){
        try {
            if(!zooKeeperService.exists(LOCK_BASE_PATH)){
                zooKeeperService.createZkNode(LOCK_BASE_PATH, null, ZooKeeperService.PERSISTENT);
                log.info("create node:{}", LOCK_BASE_PATH);
            }
            isInitSuccess = true;
        } catch (Exception e) {
            log.error("zk distributed lock init error.", e);
        }
    }


    @Override
    public String tryLock(String key, Long timeoutMilli, String value) throws DistributedLockException {


        try {
            // reentrant lock
            LockHolder lockHolder = lockHolderThreadLocal.get();

            if(Objects.nonNull(lockHolder)){
                lockHolder.getLockCount().incrementAndGet();
                return lockHolder.getLockPath();
            }
            return attemptGetLock(key, timeoutMilli, value);
        } catch (DistributedLockException e){
          throw e;
        } catch (Exception e) {
            log.info("distributed lock error.", e);
            throw new DistributedLockException(AppExceptionEnum.ACQUIRE_DISTRIBUTED_LOCK_EXCEPTION);
        }


    }

    public String attemptGetLock(String key, Long timeoutMilli, String value) throws Exception{
        String nodeName = LOCK_BASE_PATH +"/" + key + SEPARATOR;

        ConfigNode node = null;
        try {
            node = zooKeeperService.createZkNode( nodeName, value, ZooKeeperService.EPHEMERAL_SEQUENTIAL);

            List<String> subNodeNames = zooKeeperService.getChildren(LOCK_BASE_PATH);
            List<String> targetSubNames = subNodeNames.stream().filter( n -> n.startsWith(key)).collect(Collectors.toList());
            Collections.sort(targetSubNames);

            if(node.getId().contains(targetSubNames.get(0))){
                // get lock
                log.info("acquired distributed lock. key={}, node:{}", key, node.getId());
                LockHolder lockHolder = new LockHolder(Thread.currentThread(), node.getId());
                lockHolderThreadLocal.set(lockHolder);
                return node.getId();
            }

            String preNode = node.getId().substring(node.getId().lastIndexOf("/") + 1);
            int preNodePos = Collections.binarySearch(targetSubNames, preNode) - 1;
            String waitLockNodeName = targetSubNames.get(preNodePos);

            waitToGetLock( LOCK_BASE_PATH + "/" + waitLockNodeName, timeoutMilli, node.getId());
        } catch (Exception e) {
            if(node != null){
                // 获取锁失败，但是要删除已经创建的临时节点
                zooKeeperService.deleteNode(node.getId());
            }
            throw e;
        }

        LockHolder lockHolder = new LockHolder(Thread.currentThread(), node.getId());
        lockHolderThreadLocal.set(lockHolder);

        log.info("acquired distributed lock after wait lock. key={}, current node={}", key, node.getId());

        return node.getId();
    }


    private boolean waitToGetLock(String preLockNode, Long timeoutMilli, String currentNode) throws Exception{

        log.info("waiting lock, preLockNode:{}, current node:{}", preLockNode, currentNode);

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        CountDownLatch countDownLatch = new CountDownLatch(1);;
        Integer version = zooKeeperService.exists(preLockNode, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                if(event.getType() == Event.EventType.NodeDeleted && countDownLatch != null){
                    log.info("count down.");
                    countDownLatch.countDown();
                }
            }
        });

        if(version != null){
            if(!countDownLatch.await(timeoutMilli, TimeUnit.MILLISECONDS)){
                throw new DistributedLockException(AppExceptionEnum.ACQUIRE_DISTRIBUTED_LOCK_TIMEOUT);
            }
        }

        stopWatch.stop();
        log.info("waiting lock, node:{}, cost:{}", preLockNode, stopWatch.getTotalTimeMillis());

        return true;
    }

    @Override
    public void releaseLock(String key) throws DistributedLockException {
        // 先释放锁，无需任何条件
        try {
            lockHolderThreadLocal.remove();
            zooKeeperService.deleteNode(key);
        } catch (Exception e) {
            log.error("delete node error. key={}", key, e);
        } finally {
            log.info("release lock. key:{}", key);
        }
    }

    @Override
    public boolean isValid() {
        return isInitSuccess();
    }


    public static class LockHolder {

        @Getter
        private Thread currentThread;
        @Getter
        private AtomicLong lockCount;
        @Getter
        private String lockPath;

        public LockHolder(Thread currentThread, String lockPath) {
            this.currentThread = currentThread;
            this.lockPath = lockPath;
            lockCount = new AtomicLong(1);
        }
    }
}
