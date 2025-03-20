package org.kylin.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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

import java.io.File;
import java.util.Optional;
import java.util.Scanner;

@Configuration
@Slf4j
public class ESConfig {
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
                new UsernamePasswordCredentials(userInner != null ? userInner:"elastic", credentialsKeyInner));

        RestClient restClient = RestClient
                .builder(new HttpHost(hostInner,  portInner, "http")
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

        // 1.从本地配置文件获取配置：先判断本地文件是否存在，再读取文件内容，反序列化为ESClusterConfiguration对象
        String configStr = getConfigurationContent();
        if(StringUtils.isNotBlank(configStr)){
            ESClusterConfiguration esClusterConfiguration = JSON.parseObject(configStr, ESClusterConfiguration.class);
            return Optional.of(esClusterConfiguration);
        }

        // 2.获取远程配置
        try {
            return OkHttpUtils.doGet(configurationUrl, ESClusterConfiguration.class, null);
        } catch (Exception e) {
            log.error("get es cluster configuration error, url:{}", configurationUrl, e);
        }
        return Optional.empty();
    }

    private String getConfigurationContent() {
        String userDir = System.getProperty("user.home");
        String filePath =  userDir + "/.zx_config/wyf_es_config.json";
        File configFile = new File(filePath);
        if(configFile.exists()){
            try {
                Scanner scanner = new Scanner(configFile);
                StringBuilder sb = new StringBuilder();
                while (scanner.hasNextLine()){
                    sb.append(scanner.nextLine());
                }
                scanner.close();
                return sb.toString();
            } catch (Exception e) {
                log.error("read es cluster configuration file error, file:{}", filePath, e);
            }
        }
        return null;
    }

}
