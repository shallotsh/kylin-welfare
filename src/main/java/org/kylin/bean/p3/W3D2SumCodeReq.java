package org.kylin.bean.p3;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import org.kylin.bean.BaseCodeReq;

@Data
public class W3D2SumCodeReq extends BaseCodeReq {

    /**
     * 二和尾
     */
    private String binSumValues;


    /**
     * 胆频选
     */
    private String boldFreqValues;

    /**
     * 钓叟选码
     */
    private String fishManCode;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
