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
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Slf4j
public class OkHttpUtils {

    private static final String DRAW_NOTICE_URL_TPL = "http://www.cwl.gov.cn/cwl_admin/kjxx/findDrawNotice?name={0}&issueCount={1}";




    public static Optional<String> doGet(String url) throws NeedRetryExcetpion{

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("referer", "http://www.cwl.gov.cn/kjxx/fc3d/kjgg/")
                .addHeader("content-type", "application/json")
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
        }
        return Optional.empty();
    }


    public static Optional<SdDrawNoticeResult> getSdDrawNoticeResult(String name, Integer issueCount){

        String url = MessageFormat.format(DRAW_NOTICE_URL_TPL, name, issueCount);

        Retryer<Optional<String>> retryer = RetryUtils.build(200L, 3);
        Optional<String> retOpt = null;
        try {
            retOpt = retryer.call(() -> OkHttpUtils.doGet(url));
        } catch (ExecutionException e) {
            log.info("query execution error.", e);
            return Optional.empty();
        } catch (RetryException e) {
            log.info("query nothing after retry.", e);
            return Optional.empty();
        }

        return Optional.ofNullable(retOpt.map(ret -> JSON.parseObject(ret, SdDrawNoticeResult.class)).orElseGet(null));
    }

}
