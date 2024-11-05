package org.kylin.wrapper;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kylin.bean.sd.SdDrawNoticeResult;
import org.kylin.constant.ESIndexEnum;
import org.kylin.util.OkHttpUtils;
import org.kylin.wrapper.bo.SdDrawResult;
import org.kylin.wrapper.bo.Shop;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class ESWrapperTest {

    @Resource
    private ESWrapper esWrapper;

    @Test
    public void testGetDocById(){
        SdDrawNoticeResult ret = esWrapper.getDocById("welfare_result", "2024110", SdDrawNoticeResult.class);
        log.info("查询ES返回resp:{}", ret);
        Assert.isTrue(ret != null, "查询结果为空");
    }

    @Test
    public void testExistsById(){
        boolean ret = esWrapper.exists("shop", "1002");
        Assert.isTrue(ret, "1002不存在");
    }


    @Test
    public void testIndex(){

        Shop shop = new Shop();
        shop.setShopid(2000L);
        shop.setShopname("测试门店");
        shop.setCityid(100);
        shop.setAddress("北京");

        esWrapper.index("shop", String.valueOf(shop.getShopid()), shop);

    }

    @Test
    public void testGetDrawResult(){

        LocalDate drawDate = LocalDate.now().minusDays(1);
        Optional<SdDrawNoticeResult> retOpt = OkHttpUtils.getSdDrawNoticeResult(drawDate, drawDate);;
        retOpt.ifPresent(ret -> {
            // 记录结果
            SdDrawResult res = SdDrawResult.from(ret.getResult().get(0));
            if(!esWrapper.exists(ESIndexEnum.WELFARE_RESULT.getIndex(), res.getCode())) {
                esWrapper.index(ESIndexEnum.WELFARE_RESULT.getIndex(), res.getCode(), res);
            }
        });
    }

}
