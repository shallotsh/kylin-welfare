package org.kylin.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConfigNode {
    private String id;
    private String value;
    private String type;
    private int version;
    private Boolean isEphemeral;
}
