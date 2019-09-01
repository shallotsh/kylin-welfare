package org.kylin.algorithm.lock.impl;

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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ZkDistributedLock implements DistributedLock {

    private static final String LOCK_BASE_PATH = "/locks";
    private static final String SEPARATOR = "_";

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

        String nodeName = LOCK_BASE_PATH +"/" + key + SEPARATOR;
        try {
            ConfigNode node = zooKeeperService.createZkNode( nodeName, value, ZooKeeperService.EPHEMERAL_SEQUENTIAL);
            List<String> subNodeNames = zooKeeperService.getChildren(LOCK_BASE_PATH);
            List<String> targetSubNames = subNodeNames.stream().filter( n -> n.startsWith(key)).collect(Collectors.toList());
            Collections.sort(targetSubNames);
            if(node.getId().contains(targetSubNames.get(0))){
                // get lock
                log.info("acquired distributed lock. key={}, node:{}", key, node.getId());
                return node.getId();
            }

            String preNode = node.getId().substring(node.getId().lastIndexOf("/") + 1);
            int preNodePos = Collections.binarySearch(targetSubNames, preNode) - 1;
            String waitLockNodeName = targetSubNames.get(preNodePos);

            waitLock( LOCK_BASE_PATH + "/" + waitLockNodeName, timeoutMilli, node.getId());
            log.info("acquired distributed lock after wait lock. key={}, node={}", key, node.getId());
            return node.getId();
        } catch (DistributedLockException e){
          throw e;
        } catch (Exception e) {
            log.info("distributed lock error.", e);
            throw new DistributedLockException(AppExceptionEnum.ACQUIRE_DISTRIBUTED_LOCK_EXCEPTION);
        }


    }

    private boolean waitLock(String preLockNode, Long timeoutMilli, String currentNode) throws Exception{

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
    public void releaseLock(String key, String value) throws DistributedLockException {
        // 先释放锁，无需任何条件
        try {
            zooKeeperService.deleteNode(key);
        } catch (Exception e) {
            log.error("delete node error. key={}", key, e);
        }
    }

    @Override
    public boolean isValid() {
        return isInitSuccess();
    }
}
