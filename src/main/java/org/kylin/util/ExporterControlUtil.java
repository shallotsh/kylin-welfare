package org.kylin.util;

import org.kylin.constant.ExportPatternEnum;

public class ExporterControlUtil {
    private static ThreadLocal<ExportPatternEnum> exportPatternEnumThreadLocal = new ThreadLocal<>();

    public static boolean setPatternType(ExportPatternEnum exportPatternEnum){
        if(exportPatternEnumThreadLocal.get() == null){
            exportPatternEnumThreadLocal.set(exportPatternEnum);
            return true;
        }
        return false;
    }

    public static void clearPatternType(){
        exportPatternEnumThreadLocal.set(null);
    }

    public static ExportPatternEnum getPatternType(){
        if(exportPatternEnumThreadLocal.get() == null){
            return null;
        }
        return exportPatternEnumThreadLocal.get();
    }
}
