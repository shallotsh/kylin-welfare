package org.kylin.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConfigReq {
    private String id;
    private String value;
    private Integer nodeType = 0;
    private int version = -1;
}
