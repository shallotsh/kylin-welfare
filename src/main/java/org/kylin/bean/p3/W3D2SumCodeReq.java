package org.kylin.bean.p3;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import org.kylin.bean.BaseCodeReq;

@Data
public class W3D2SumCodeReq extends BaseCodeReq {

    private String binSumValues;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
