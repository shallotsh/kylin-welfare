package org.kylin.util;

import com.alibaba.fastjson.JSON;
import com.github.rholder.retry.RetryException;
import com.github.rholder.retry.Retryer;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.kylin.bean.sd.SdDrawNoticeResult;
import org.kylin.exception.NeedRetryExcetpion;

import java.io.IOException;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Slf4j
public class OkHttpUtils {

    private static final String DRAW_NOTICE_URL_TPL = "http://www.cwl.gov.cn/cwl_admin/front/cwlkj/search/kjxx/findDrawNotice?name=3d&issueCount=&issueStart=&issueEnd=&dayStart={0}&dayEnd={1}";




    public static Optional<String> doGet(String url) throws NeedRetryExcetpion{

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("cookie", "HMF_CI=42185cefd9722e8900c2652100e4a935ad5a0f6744b15039d39bbe2d657aef2428; 21_vq=1; _dd_s=logs=1&id=4facbb46-605c-4c35-9cd9-608c7c66c2fa&created=1654343603149")
                .addHeader("host", "www.cwl.gov.cn")
                .addHeader("referer", "http://www.cwl.gov.cn/ygkj/wqkjgg/fc3d/")
                .addHeader("content-type", "application/json")
                .addHeader("user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/102.0.5005.61 Safari/537.36")
                .build();

        Response response = null;
        try {
            response = client.newCall(request).execute();
            if(response.isSuccessful()) {
                return Optional.of(response.body().string());
            }else{
                throw new NeedRetryExcetpion("query lottery code error, retry.");
            }
        } catch (IOException e) {
            log.info("doGet error", e);
        } finally {
            if(response != null){
                response.close();
            }
        }
        return Optional.empty();
    }


    public static Optional<SdDrawNoticeResult> getSdDrawNoticeResult(LocalDate beginDate, LocalDate endDate){


        String url = MessageFormat.format(DRAW_NOTICE_URL_TPL, beginDate.toString(), endDate.toString());

        Retryer<Optional<String>> retryer = RetryUtils.build(200L, 3);
        Optional<String> retOpt = null;
        try {
            retOpt = retryer.call(() -> OkHttpUtils.doGet(url));
        } catch (ExecutionException e) {
            log.info("query execution error.", e);
            return Optional.empty();
        } catch (RetryException e) {
            //log.info("query nothing after retry.", e);
            return Optional.empty();
        }

        return Optional.ofNullable(retOpt.map(ret -> JSON.parseObject(ret, SdDrawNoticeResult.class)).orElseGet(null));
    }

}
