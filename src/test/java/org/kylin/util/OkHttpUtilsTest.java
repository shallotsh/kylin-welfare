package org.kylin.util;

import org.junit.Test;
import org.kylin.bean.sd.SdDrawNoticeResult;
import org.springframework.boot.test.context.SpringBootTest;
import sun.jvm.hotspot.utilities.Assert;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.Assert.*;

public class OkHttpUtilsTest {

    @Test
    public void getSdDrawNoticeResult() {
        LocalDate drawDate = LocalDate.now().minusDays(1);
        Optional<SdDrawNoticeResult> resultOptional=  OkHttpUtils.getSdDrawNoticeResult(drawDate, drawDate);
        Assert.that(resultOptional.isPresent(), "查询结果为空");
    }
}