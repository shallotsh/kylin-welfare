package org.kylin.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

/**
 * @author shallotsh
 * @date 2017/7/16 下午5:08.
 */
@Slf4j
public class CommonUtils {

    public static String getCurrentTimeString(){
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    public static String getCurrentDateString(){
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(currentTime);
        return dateString;
    }


    public static String createIfNotExist(String parentPath, String subPath) throws IOException {

        Objects.requireNonNull(parentPath, "parentPath can not be null.");
        Objects.requireNonNull(subPath, "subPath can not be null.");
        String fullPath;
        if(!parentPath.endsWith("/")){
            fullPath = parentPath + File.separator + subPath;
        }else{
            fullPath = parentPath + subPath;
        }
        if(createDirIfNotExist(fullPath)){
            return fullPath;
        }else{
            throw new IOException("create target directory error. target directory:" + fullPath);
        }

    }


    public static boolean createDirIfNotExist(String destDirName) {
        File dir = new File(destDirName);
        if (dir.exists()) {
            return true;
        }
        if (!destDirName.endsWith(File.separator)) {
            destDirName = destDirName + File.separator;
        }

        //创建目录
        if (dir.mkdirs()) {
            log.debug("创建目录" + destDirName + "成功！");
            return true;
        } else {
            log.debug("创建目录" + destDirName + "失败！");
            return false;
        }
    }

    public static String getIp(HttpServletRequest request) {

        try {
            String ip = request.getHeader("X-Real-IP");
            if (StringUtils.isNotEmpty(ip) && !"unknown".equalsIgnoreCase(ip)) {
                return ip;
            }
            ip = request.getHeader("X-Forwarded-For");
            if (StringUtils.isNotEmpty(ip) && !"unknown".equalsIgnoreCase(ip)) {
                int index = ip.indexOf(",");
                if (index != -1) {
                    return ip.substring(0, index);
                } else {
                    return ip;
                }
            } else {
                return request.getRemoteAddr();
            }
        } catch (Exception e) {
            return request.getRemoteAddr();
        }
    }


    public static Optional<String> getAgent(HttpServletRequest request){
        if(Objects.isNull(request)){
            return Optional.empty();
        }

        String agent = request.getHeader("user-agent");
        log.info("user-agent={}", agent);

        return Optional.ofNullable(agent);
    }

    public static boolean isGoogleBrowser(HttpServletRequest request){
        Optional<String> agentOpt = getAgent(request);

        return agentOpt.map(agent -> agent.contains("Chrome"))
                .orElse(Boolean.FALSE);
    }

}
