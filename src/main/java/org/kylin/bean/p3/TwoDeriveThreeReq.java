package org.kylin.bean.p3;

import lombok.Data;
import lombok.ToString;
import org.kylin.bean.BaseCodeReq;


@Data
@ToString(callSuper = true)
public class TwoDeriveThreeReq extends BaseCodeReq {
    /**
     * 顺序杀序列
     */
    private String seqKill;

    /**
     * 二码和杀
     */
    private String twoCodeSumSeq;
}
