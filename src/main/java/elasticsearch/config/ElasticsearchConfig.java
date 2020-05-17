package elasticsearch.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticsearchConfig {

    @Value("${elasticsearch.address}")
    private String address;

    @Bean
    public RestHighLevelClient getRestHighLevelClient(){
        return  new  RestHighLevelClient(RestClient.builder(
                new HttpHost("localhost", 9200, "http")));
    }
}
