package org.kylin.bean.p3;

import lombok.Data;
import org.kylin.bean.BaseCodeReq;


@Data
public class TwoDeriveThreeReq extends BaseCodeReq {
    /**
     * 顺序杀序列
     */
    private String seqKill;
}
