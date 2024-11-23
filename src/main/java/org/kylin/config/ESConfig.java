package org.kylin.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.alibaba.fastjson.JSON;
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
import org.kylin.util.OkHttpUtils;
import org.kylin.wrapper.bo.ESClusterConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

@Configuration
@Slf4j
public class ESConfig {

    @Value("${es.host}")
    private String hostDefault;
    @Value("${es.port}")
    private Integer portDefault;
    @Value("${es.pwd}")
    private String pwdDefault;

    @Value("${config.es-config-url}")
    private String configurationUrl ;

    @Bean
    public ElasticsearchClient esClient() {

        ESClusterConfiguration esClusterConfiguration = esClusterConfiguration().orElse(null);
        log.info("es cluster configuration: {}", JSON.toJSON(esClusterConfiguration));
        if (esClusterConfiguration == null) {
            log.error("es cluster configuration is null, use default config.");
            return null;
        }

        String hostInner = esClusterConfiguration.getHost();
        Integer portInner = esClusterConfiguration.getPort();
        String userInner = esClusterConfiguration.getUser();
        String credentialsKeyInner = esClusterConfiguration.getPwd();

        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(userInner != null ? userInner:"elastic",
                     credentialsKeyInner != null ? credentialsKeyInner: pwdDefault));

        RestClient restClient = RestClient
                .builder(new HttpHost(hostInner != null ? hostInner : hostDefault,  portInner != null ? portInner : portDefault, "http")
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


    private Optional<ESClusterConfiguration> esClusterConfiguration() {
        try {
            return OkHttpUtils.doGet(configurationUrl, ESClusterConfiguration.class, null);
        } catch (Exception e) {
            log.error("get es cluster configuration error, url:{}", configurationUrl, e);
        }
        return Optional.empty();
    }
}
