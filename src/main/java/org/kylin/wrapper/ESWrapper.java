package org.kylin.wrapper;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.ExistsRequest;
import co.elastic.clients.elasticsearch.core.GetRequest;
import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.transport.endpoints.BooleanResponse;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;

@Service
@Slf4j
public class ESWrapper {

    private String ES_DEFAULT_INDEX_NAME = "shop";

    @Resource
    private ElasticsearchClient client;


    public <T> T getDocById(String index, String id, Class<T> docType){

        try {

            GetRequest request = new GetRequest.Builder()
                    .index(index)
                    .id(id).build();

            GetResponse<T> response = client.get(request, docType);
            if(response.found()){
                log.info("es resp:{}", JSON.toJSONString(response.getClass()));
                return response.source();
            }else{
                log.info("ES return empty, index:{}, id:{}", index, id);
            }

        } catch (IOException e){
            log.info("查询ES报错 index:{}, id:{}", index, id, e);
        }
        return null;
    }


    public boolean exists(String index, String id) {
        try {
            BooleanResponse response = client.exists(e -> e
                    .index(index)
                    .id(id)
            );
            return response.value();
        }catch (IOException e){
            log.info("es判断是否存在 index:{}, id:{}", index, id, e);
        }

        // 默认返回false
        return false;
    }


    public void index(String index, String id, Object data){
        try {
            IndexResponse resp = client.index(i -> i
                    .index(index)
                    .id(id)
                    .document(data)
            );
        }catch (IOException e){
            log.info("索引数据出错 index:{}, id:{}, data:{}", index, id, JSON.toJSONString(data), e);
        }
    }


}
