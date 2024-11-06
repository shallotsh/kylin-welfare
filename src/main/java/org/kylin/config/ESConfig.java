package org.kylin.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder.HttpClientConfigCallback;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class ESConfig {

    @Value("${es.host}")
    private String hostDefault;
    @Value("${es.port}")
    private Integer portDefault;
    @Value("${es.pwd}")
    private String pwdDefault;


    @Bean
    public ElasticsearchClient esClient() {

        String hostInner = System.getenv("ES_HOST");
        String portInner = System.getenv("ES_PORT");
        String userInner = System.getenv("ES_USR");
        String credentialsKeyInner = System.getenv("ES_PWD");

        log.info("from env, host: {}, port:{}, user:{}, key:{}", hostInner, portInner, userInner, credentialsKeyInner);

        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(userInner != null ? userInner:"elastic",
                     credentialsKeyInner != null ? credentialsKeyInner: pwdDefault));

        RestClient restClient = RestClient
                .builder(new HttpHost(hostInner != null ? hostInner : hostDefault,  portInner != null ? NumberUtils.toInt(portInner): portDefault, "http")
                ).setHttpClientConfigCallback(new HttpClientConfigCallback() {
                    @Override
                    public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
                        return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                    }
                })
                .setRequestConfigCallback( builder -> {
                    builder.setConnectTimeout(1000);
                    builder.setSocketTimeout(3000);
                    return builder;
                })
                .build();

        ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());

        return new ElasticsearchClient(transport);
    }

}
