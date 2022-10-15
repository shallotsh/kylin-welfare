package org.kylin.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

@Slf4j
@RestController
@RequestMapping("/api")
public class ApiController {
    @RequestMapping(value = "/monitor/alive", method = RequestMethod.GET)
    public Map<String, String> check(){
        Map<String, String> status = new HashMap<>();
        status.put("status", "ok");
        return status;
    }


    @RequestMapping(value = "/async/monitor/alive", method = RequestMethod.GET)
    public Callable<Map<String, String>> checkAsync(){

        log.info("start monitor.");

        Callable<Map<String, String>> status = () -> {

            Thread.sleep(100);


            Map<String, String> st = new HashMap<>();
            st.put("status", "ok");

            log.debug("complete check status.");

            return st;

        };

        log.debug("return monitor.");

        return status;
    }

    @RequestMapping(value = "/monitor/mem", method = RequestMethod.GET)
    public Map<String, String> checkMem() {
        Map<String, String> mem = new HashMap<>();
        //
        Runtime run = Runtime.getRuntime();
        double max = run.maxMemory() / 1024.0 / 1024.0;
        double total = run.totalMemory() / 1024.0 / 1024.0;
        double free = run.freeMemory() / 1024.0 / 1024.0;
        double usable = max - total + free;
        DecimalFormat format = new DecimalFormat("#.00");
        mem.put("最大内存", format.format(max));
        mem.put("总分配内存", format.format(total));
        mem.put("未使用内存", format.format(free));
        mem.put("可用内存", format.format(usable));

        try {
            InetAddress addr = InetAddress.getLocalHost();
            mem.put("hostAddress", addr.getHostAddress());
            mem.put("hostName", addr.getHostName());
        } catch (Exception e){

        }

        return mem;
    }
}
