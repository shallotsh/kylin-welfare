package org.kylin.bean.p3;

import com.alibaba.fastjson.JSON;
import lombok.Getter;
import lombok.Setter;
import org.kylin.bean.BaseCodeReq;


public class ExpertCodeReq extends BaseCodeReq {

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
