package org.kylin.wrapper.bo;


import lombok.Data;

@Data
public class ESClusterConfiguration {
    private String host;
    private Integer port;
    private String user;
    private String pwd;
}
