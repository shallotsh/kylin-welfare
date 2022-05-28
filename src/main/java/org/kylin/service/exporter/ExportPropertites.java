package org.kylin.service.exporter;

import lombok.Data;

@Data
public class ExportPropertites {
    /**
     * 频度下限值
     */
    private Integer freqLowLimit;
    /**
     * 文件前缀
     */
    private String filePrefix;

    public Integer getFreqLowLimitValue() {
        return freqLowLimit == null ? 0 : getFreqLowLimit();
    }
}
