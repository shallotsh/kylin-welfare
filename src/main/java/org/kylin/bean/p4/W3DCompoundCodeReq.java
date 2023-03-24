package org.kylin.bean.p4;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.kylin.bean.BaseCodeReq;
import org.kylin.bean.LabelValue;
import org.kylin.bean.p5.WCode;
import org.kylin.bean.p5.WCodeReq;

import java.util.Arrays;
import java.util.List;

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

    /**
     * 已删除code码，用于杀四码导出场景
     */
    private List<LabelValue<List<WCode>>> deletedCodes;


    @Override
    public WCodeReq adaptToWCodeReq() {
        WCodeReq req = super.adaptToWCodeReq();
        if(CollectionUtils.isNotEmpty(deletedCodes)) {
            req.setDeletedCodes(deletedCodes);
        }
        return req;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
