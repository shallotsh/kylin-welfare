package org.kylin.wrapper;

import com.alibaba.fastjson.JSON;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;

public class ZkProSync implements Watcher {

    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);

    private static ZooKeeper zk = null;
    private static Stat stat = new Stat();

    @Override
    public void process(WatchedEvent watchedEvent) {
        if(Event.KeeperState.SyncConnected != watchedEvent.getState()){
            return;
        }

        if(Event.EventType.None == watchedEvent.getType() && null == watchedEvent.getPath()){
            connectedSemaphore.countDown();
        } else if(watchedEvent.getType() == Event.EventType.NodeDataChanged){
            try {
                System.out.println("配置已更改, 新值为:" + new String(zk.getData(watchedEvent.getPath(), true, stat))
                        + ",id:" + watchedEvent.getPath());
            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws Exception{
        String path = "/username";
        zk = new ZooKeeper("127.0.0.1:2181", 5000, new ZkProSync());

//        System.out.println(new String(zk.getData(id, true, stat)));
//        Stat stat = zk.setData(id, "Nice".getBytes(), 3);
//        System.out.println(JSON.toJSONString(stat));

//        String ret = zk.create("/distributed/lock_hyw", null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        byte[] ret = zk.getData("/distributed/lock_hyw", true, new Stat());
        System.out.println("ret:" + new String(ret));



        connectedSemaphore.await();

        Thread.sleep(Integer.MAX_VALUE);
    }
}
