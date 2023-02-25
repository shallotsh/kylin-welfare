package org.kylin.bean.p4;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import org.kylin.bean.BaseCodeReq;

@Data
public class W3DCompoundCodeReq extends BaseCodeReq {

    /**
     * 晚秋选码
     */
    private String lateAutumnCode;

    /**
     * 四转三命令
     */
    private Boolean fourToThreeCmd;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
