package org.kylin.adapter.zk;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.kylin.algorithm.Validator;
import org.kylin.bean.ConfigNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class ZooKeeperService implements Watcher, Validator {

    public static final Integer PERSISTENT = 0;
    public static final Integer PERSISTENT_SEQUENTIAL = 2;
    public static final Integer EPHEMERAL = 1;
    public static final Integer EPHEMERAL_SEQUENTIAL = 3;
    public static final Integer CONTAINER = 4;
    public static final Integer PERSISTENT_WITH_TTL = 5;

    private String serverList = "127.0.0.1:2181,127.0.0.1:21822,127.0.0.1:2183";

    @Value("zk.session-timeout")
    private String sessionTimeout;

    private ZooKeeper zk;
    private CountDownLatch connectedSemaphore=new CountDownLatch(1);


    @Override
    public void process(WatchedEvent event) {
        if(event.getState() == Event.KeeperState.SyncConnected){
            if(Event.EventType.None == event.getType() && event.getPath() == null && connectedSemaphore.getCount() > 0) {
                connectedSemaphore.countDown();
                return;
            }


        } else  if(event.getType() == Event.EventType.NodeDataChanged){
            try {
                log.info("node:{}, new value:{}", event.getPath(), zk.getData(event.getPath(), this, new Stat()));
            } catch (Exception e) {
                log.error("read zk data error, node:{}", event.getPath(), e);
            }
        }
    }


    @PostConstruct
    public void init(){
        try {
            zk = new ZooKeeper(serverList, NumberUtils.toInt(sessionTimeout, 5000),this);
            connectedSemaphore.await(3000L, TimeUnit.MILLISECONDS);
//
//            if(!zk.getState().isConnected()){
//                return ;
//            }
//
//            List<String> nodeList = zk.getChildren("/username", true);
//            log.info("nodeList:{}", nodeList);


        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        log.info("connect to zookeeper success. state:{}", zk.getState());

    }


    public ConfigNode createZkNode(String path, String value, int nodeType) throws Exception{
        if(!validate()){
            throw new RuntimeException("zk is not connected to server.");
        }

        CreateMode createMode = CreateMode.fromFlag(nodeType, CreateMode.PERSISTENT);
        Stat stat = new Stat();
        String nodePath = zk.create(path, value == null ? new byte[0] : value.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, createMode, stat);
        return ConfigNode.builder()
                .id(nodePath)
                .value(value)
                .type(createMode.toString())
                .version(stat.getVersion())
                .isEphemeral(createMode.isEphemeral())
                .build();
    }

    public int setValueOfNode(String path, String value, int version) throws Exception{
        Stat stat = zk.setData(path, value.getBytes(), version);
        return stat.getVersion();
    }


    public ConfigNode getValueOfNode(String path) throws Exception{
        if(!validate()){
            throw new RuntimeException("zk is not connected to server.");
        }

        Stat stat = new Stat();
        byte[] value = zk.getData(path, this, stat);
        return ConfigNode.builder()
                .id(path)
                .value(new String(value))
                .version(stat.getVersion())
                .isEphemeral(stat.getEphemeralOwner() != 0)
                .build();
    }

    public List<String> getChildren(String parentPath) throws Exception{
        if(!validate()){
            throw new RuntimeException("zk is not connected to server.");
        }
        Stat stat = new Stat();
        List<String> children = zk.getChildren(parentPath, true);
        Collections.sort(children);
        return children;
    }


    public void deleteNode(String path) throws Exception{
        if(!validate()){
            throw new RuntimeException("zk is not connected to server.");
        }

        zk.delete(path, -1);
    }


    public boolean exists(String path) throws Exception{
        if(!validate()){
            throw new RuntimeException("zk is not connected to server.");
        }

        return zk.exists(path, Boolean.TRUE) != null;

    }

    public Integer exists(String path, Watcher watcher) throws Exception{
        if(!validate()){
            throw new RuntimeException("zk is not connected to server.");
        }
        Stat stat = zk.exists(path, watcher);
        if(stat != null){
            return stat.getVersion();
        }else{
            return null;
        }
    }


    @Override
    public boolean validate() {
        return Optional.ofNullable(zk)
                .map(zoo -> zoo.getState().isConnected())
                .orElse(false);
    }

    @PreDestroy
    public void destroy(){
        Optional.ofNullable(zk).ifPresent(zoo ->{
            try {
                zoo.close();
                log.info("close zk connection success.");
            } catch (InterruptedException e) {
                log.warn("close zk exception.", e);
            }
        });
    }

}
