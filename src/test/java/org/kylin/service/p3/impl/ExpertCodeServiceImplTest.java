package org.kylin.service.p3.impl;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.kylin.bean.p5.WCode;
import org.kylin.util.WCodeUtils;

import java.util.ArrayList;
import java.util.List;

class ExpertCodeServiceImplTest {

    @Test
    void convertTo2DCodesForEveryFreq() {
        List<WCode> wCodes = new ArrayList<>();
        wCodes.add(new WCode(3, 1,2,1));
        wCodes.get(0).setFreq(3);

        List<WCode> ret = WCodeUtils.convert3DTo2D(wCodes);
    }
}