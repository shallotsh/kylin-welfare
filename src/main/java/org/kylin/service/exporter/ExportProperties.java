package org.kylin.service.exporter;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public class ExportProperties {
    /**
     * 频度下限值
     */
    private Integer freqLowLimit;
    /**
     * 文件前缀
     */
    private String filePrefix;
    /**
     * 文件名
     */
    private String fileName;

    public Integer getFreqLowLimitValue() {
        return freqLowLimit == null ? 0 : getFreqLowLimit();
    }

    public String getFileNme(){
        if(StringUtils.isBlank(fileName)){
            return null;
        }
        StringBuilder sb = new StringBuilder();
        if(StringUtils.isNotBlank(filePrefix)){
            sb.append(filePrefix);
        }
        sb.append(fileName);
        return sb.toString();
    }
}
