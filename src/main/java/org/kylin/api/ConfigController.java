package org.kylin.api;

import ch.qos.logback.core.util.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.kylin.adapter.zk.ZooKeeperService;
import org.kylin.algorithm.lock.impl.ZkDistributedLock;
import org.kylin.bean.*;
import org.kylin.exception.DistributedLockException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/api/zk")
@Slf4j
public class ConfigController {

    @Resource
    private ZooKeeperService zooKeeperService;

    @Resource
    private ZkDistributedLock zkDistributedLock;

    @ResponseBody
    @RequestMapping(value = "/node", method = RequestMethod.PUT)
    public WyfResponse createNode(@RequestBody ConfigReq configReq){
        try {
            ConfigNode node = zooKeeperService.createZkNode(configReq.getId(), configReq.getValue(), configReq.getNodeType());
            return WyfDataResponse.of(node);
        } catch (Exception e) {
            log.error("创建节点失败, configReq:{}", configReq, e);
            return WyfErrorResponse.buildErrorResponse();
        }
    }

    @ResponseBody
    @RequestMapping(value = "/node", method = RequestMethod.POST)
    public WyfResponse setValueOfNode(@RequestBody ConfigReq configReq){

        try {
            int version = zooKeeperService.setValueOfNode(configReq.getId(), configReq.getValue(), configReq.getVersion());
            return WyfDataResponse.of(version);
        } catch (Exception e) {
            log.error("更新节点失败, configReq:{}", configReq, e);
            return WyfErrorResponse.buildErrorResponse();
        }

    }

    @ResponseBody
    @RequestMapping(value = "/node", method = RequestMethod.GET)
    public WyfResponse getValueOfNode(String path){
        try {
            ConfigNode configNode = zooKeeperService.getValueOfNode(path);
            return WyfDataResponse.of(configNode);
        } catch (Exception e) {
            log.info("查询zk节点错误, id:{}", path, e);
            return WyfErrorResponse.buildErrorResponse();
        }
    }

    @ResponseBody
    @RequestMapping(value = "/node/children", method = RequestMethod.GET)
    public WyfResponse getChildrenOfNode(String path){
        try {
            List<String> children = zooKeeperService.getChildren(path);
            return WyfDataResponse.of(children);
        } catch (Exception e) {
            log.info("查询孩子节点错误, id:{}", path, e);
            return WyfErrorResponse.buildErrorResponse();
        }
    }


    @ResponseBody
    @RequestMapping(value = "/node", method = RequestMethod.DELETE)
    public WyfResponse deleteNode(@RequestBody ConfigReq configReq){
        try {
            zooKeeperService.deleteNode(configReq.getId());
            return WyfDataResponse.of(Boolean.TRUE);
        } catch (Exception e) {
            log.info("删除节点错误, req:{}", configReq, e);
            return WyfErrorResponse.buildErrorResponse();
        }
    }

    @ResponseBody
    @RequestMapping(value = "/lock", method = RequestMethod.GET)
    public WyfResponse tryLock(Long timeout) {
        Long val = System.currentTimeMillis();
        try {

            String lockNode = zkDistributedLock.tryLock("seek", timeout, String.valueOf(val));
            try {
                TimeUnit.SECONDS.sleep(5);
                String ret = lockNode + "--> " + val;
                return WyfDataResponse.of(ret);
            } finally {
                log.info("release lock.");
                zkDistributedLock.releaseLock(lockNode, String.valueOf(val));
            }
        } catch (Exception e) {
            log.warn("try lock occurs error.", e);
            return WyfErrorResponse.buildErrorResponse();
        }
    }

}
