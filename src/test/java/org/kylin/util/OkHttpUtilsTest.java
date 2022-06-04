package org.kylin.util;

import org.junit.Assert;
import org.junit.Test;
import org.kylin.bean.sd.SdDrawNoticeResult;

import java.time.LocalDate;
import java.util.Optional;

public class OkHttpUtilsTest {

    @Test
    public void getSdDrawNoticeResult() {
        LocalDate drawDate = LocalDate.now().minusDays(1);
        Optional<SdDrawNoticeResult> resultOptional=  OkHttpUtils.getSdDrawNoticeResult(drawDate, drawDate);
        Assert.assertTrue("查询结果为空", resultOptional.isPresent());
    }
}