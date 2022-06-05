package org.kylin.util;

import lombok.experimental.UtilityClass;

import java.util.HashSet;
import java.util.Set;

@UtilityClass
public class RequestFilterUtil {
    private static final Set<String> staticResourcePrefixPath = new HashSet<>();
    private static final Set<String> noAuthPrefixPath = new HashSet<>();

    static {
        staticResourcePrefixPath.add("/css");
        staticResourcePrefixPath.add("/js");
        staticResourcePrefixPath.add("/images");
        staticResourcePrefixPath.add("/html");
        staticResourcePrefixPath.add("/fonts");
        staticResourcePrefixPath.add("/favicon");


        noAuthPrefixPath.add("/login");
    }

    public static boolean isStaticResourceRequest(String serverPath){
        for(String prefix : staticResourcePrefixPath){
            if(serverPath.startsWith(prefix)){
                return true;
            }
        }
        return false;
    }

    public static boolean isNoAuthRequest(String serverPath) {
        if(isStaticResourceRequest(serverPath)){
            return true;
        }
        for(String prefix : staticResourcePrefixPath){
            if(serverPath.startsWith(prefix)){
                return true;
            }
        }
        return false;
    }


}
