package org.kylin.bean.p3;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.kylin.bean.BaseCodeReq;
import org.kylin.bean.LabelValue;
import org.kylin.bean.p5.WCode;
import org.kylin.bean.p5.WCodeReq;

import java.util.List;


@Data
public class ExpertCodeReq extends BaseCodeReq {

    private List<LabelValue<List<WCode>>> deletedCodes;

    private String twoCodeSumSeq;

    private Integer freqLowLimit;

    @Override
    public WCodeReq adaptToWCodeReq() {
        WCodeReq req = super.adaptToWCodeReq();
        req.setDeletedCodes(deletedCodes);
        return req;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
