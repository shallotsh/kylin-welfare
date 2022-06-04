package org.kylin.util;

import lombok.experimental.UtilityClass;

import java.util.HashSet;
import java.util.Set;

@UtilityClass
public class RequestFilterUtil {
    private static final Set<String> nofilterPrefixPath = new HashSet<>();

    {
        nofilterPrefixPath.add("/css");
//        nofilterPrefixPath.add("/js");
        nofilterPrefixPath.add("/images");
        nofilterPrefixPath.add("/html");
        nofilterPrefixPath.add("/fonts");
        nofilterPrefixPath.add("/favicon");
        nofilterPrefixPath.add("/login");
    }

    public static boolean isStaticResourceRequest(String serverPath){
        for(String prefix : nofilterPrefixPath){
            if(serverPath.startsWith(prefix)){
                return true;
            }
        }
        return false;
    }


}
