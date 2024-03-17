package org.kylin.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ESConfig {


    @Bean
    public ElasticsearchClient esClient() {

        RestClient restClient = RestClient
                .builder(new HttpHost("es-server", 9200, "http")
                ).setRequestConfigCallback( builder -> {
                    builder.setConnectTimeout(1000);
                    builder.setSocketTimeout(3000);
                    return builder;
                })
                .build();

        ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());

        return new ElasticsearchClient(transport);
    }

}
