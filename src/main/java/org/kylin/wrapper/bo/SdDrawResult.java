package org.kylin.wrapper.bo;

import lombok.Data;
import org.apache.commons.lang3.math.NumberUtils;
import org.kylin.bean.sd.SdDrawOverview;

@Data
public class SdDrawResult {

    private String date;

    private String code;

    private String red;

    private Integer h;

    private Integer d;

    private Integer u;

    private String sales;

    private String poolmoney;


    public static SdDrawResult from(SdDrawOverview sdDrawOverview){

        if(sdDrawOverview == null){
            return null;
        }
        SdDrawResult ret = new SdDrawResult();
        ret.setDate(sdDrawOverview.getDate().substring(0, 10));
        ret.setCode(sdDrawOverview.getCode());
        ret.setRed(sdDrawOverview.getRed());
        ret.setSales(sdDrawOverview.getSales());
        ret.setPoolmoney(sdDrawOverview.getPoolmoney());
        String[] rds = sdDrawOverview.getRed().split(",");
        ret.setH(NumberUtils.toInt(rds[0], -1));
        ret.setD(NumberUtils.toInt(rds[1], -1));
        ret.setU(NumberUtils.toInt(rds[2], -1));

        return ret;
    }

}
