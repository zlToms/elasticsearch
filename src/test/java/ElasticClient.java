import org.apache.http.HttpHost;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Date;

public class ElasticClient {

    /*public static void main(String[] args){
        System.out.println(13);

    }*/

    RestHighLevelClient   client;

    @Before
    public void test(){
        client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http")));
    }

    @Test
    public void test01(){
        GetRequest getRequest = new GetRequest(
                "tztest",
                "user",
                "1");
        try {
            boolean exists = client.exists(getRequest, RequestOptions.DEFAULT);
            System.out.println(exists);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Test
    public void test02(){
        GetRequest getRequest = new GetRequest();



    }
}
