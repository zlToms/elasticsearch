import org.apache.http.HttpHost;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.*;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ElasticsearchDocumentAPI {

    public RestHighLevelClient client ;

    @Before
    public void test(){
        client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http")));
    }

    /**
     * Json
     * @throws IOException
     */
    @Test
    public void test01() throws IOException {
        IndexRequest request = new IndexRequest(
                "tztest",
                "user",
                "1");
        String jsonString = "{" +
                "\"user\":\"kimchy\"," +
                "\"postDate\":\"2013-01-30\"," +
                "\"message\":\"trying out Elasticsearch\"" +
                "}";
        request.source(jsonString, XContentType.JSON);

        IndexResponse indexResponse = client.index(request, RequestOptions.DEFAULT);

        System.out.println(indexResponse.getId());
        System.out.println(indexResponse.getClass());

        client.close();
    }


    /**
     * index api map
     * @throws IOException
     */
    @Test
    public void test02() throws IOException {
        Map<String, Object> jsonMap = new HashMap<String, Object>();
        jsonMap.put("user", "kimchy");
        jsonMap.put("postDate", new Date());
        jsonMap.put("message", "trying out Elasticsearch");
        IndexRequest request = new IndexRequest("posts", "doc", "3")
                .source(jsonMap);

        IndexResponse indexResponse = client.index(request, RequestOptions.DEFAULT);

        System.out.println(indexResponse.getId());
        System.out.println(indexResponse.getClass());

        client.close();
    }

    /**
     * index api  XContentBuilder
     * @throws IOException
     */
    @Test
    public void test03() throws IOException {
        XContentBuilder builder = XContentFactory.jsonBuilder();
        builder.startObject();
        {
            builder.field("user", "kimchy");
            builder.timeField("postDate", new Date());
            builder.field("message", "trying out Elasticsearch");
        }
        builder.endObject();
        IndexRequest request = new IndexRequest("posts", "doc", "1")
                .source(builder);

        IndexResponse indexResponse = client.index(request, RequestOptions.DEFAULT);

        System.out.println(indexResponse.getId());
        System.out.println(indexResponse.getClass());

        client.close();
    }

    @Test
    public void test04() throws IOException {
        IndexRequest request = new IndexRequest("tztest", "user", "1")
                .source("user", "kimchy",
                        "postDate", new Date(),
                        "message", "trying out Elasticsearch");

        IndexResponse indexResponse = client.index(request, RequestOptions.DEFAULT);

        System.out.println(indexResponse.getId());
        System.out.println(indexResponse.getClass());

        client.close();
    }


    /**
     * get api
     * @throws IOException
     */
    @Test
    public void indexGetTest01() throws IOException {
        GetRequest getRequest = new GetRequest(
                "tztest",
                "user",
                "1");

        GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);

        System.out.println(getResponse.getFields());
        System.out.println(getResponse.getSource());

        client.close();


    }

    /**
     * exists
     * @throws IOException
     */
    @Test
    public void indexGetExsitTest1() throws IOException {
        GetRequest getRequest = new GetRequest(
                "tztest",
                "user",
                "1");
        getRequest.fetchSourceContext(new FetchSourceContext(false));
        getRequest.storedFields("_none_");

        boolean exists = client.exists(getRequest, RequestOptions.DEFAULT);
        System.out.println(exists);


        client.close();
    }

    /**
     * exists
     * @throws IOException
     */
    @Test
    public void indexGetExsitAsyTest02() throws IOException {

        GetRequest getRequest = new GetRequest(
                "tztest",
                "user",
                "1");
       // getRequest.fetchSourceContext(new FetchSourceContext(false));
        //getRequest.storedFields("_none_");

        ActionListener<Boolean> listener = new ActionListener<Boolean>() {
            @Override
            public void onResponse(Boolean exists) {
                System.out.println(exists);
                System.out.println("你好:");
            }

            @Override
            public void onFailure(Exception e) {
                System.out.println(1325);
                System.out.println(e);
            }
        };
        client.existsAsync(getRequest, RequestOptions.DEFAULT, listener);

        client.close();
    }





    /**
     * update
     * @throws IOException
     */
    @Test
    public void updateTest01() throws IOException {

        UpdateRequest request = new UpdateRequest(
                "posts",
                "doc",
                "1");
        String jsonString = "{" +
                "\"updated\":\"2017-01-01\"," +
                "\"reason\":\"daily update\"" +
                "}";
        request.doc(jsonString, XContentType.JSON);


        UpdateResponse updateResponse = client.update(
                request, RequestOptions.DEFAULT);

        String index = updateResponse.getIndex();
        long version = updateResponse.getVersion();
        System.out.println(index);
        System.out.println(version);

        client.close();
    }



    /**
     * delete
     * @throws IOException
     */
    @Test
    public void deleteTest01() throws IOException {
        DeleteRequest request = new DeleteRequest (
                "posts",
                "doc",
                "1");

        DeleteResponse deleteResponse = client.delete(
                request, RequestOptions.DEFAULT);
        String index = deleteResponse.getIndex();
        long version = deleteResponse.getVersion();
        System.out.println(index);
        System.out.println(version);
        client.close();
    }

    /**
     * Bulk
     * @throws IOException
     */
    @Test
    public void bulkTest01() throws IOException {
        Map<String,String> map= new HashMap<>();
        map.put("hu","45");
        map.put("dhja","dasi");
        BulkRequest request = new BulkRequest();
        request.add(new IndexRequest("post1", "doc", "5")
        .source(map));
       /* request.add(new IndexRequest("posts", "doc", "6")
                .source(XContentType.JSON,"field", "华为"));
        request.add(new IndexRequest("posts", "doc", "7")
                .source(XContentType.JSON,"field", "海康"));*/

        BulkResponse bulkResponse = client.bulk(request, RequestOptions.DEFAULT);
        if (bulkResponse.hasFailures()) {
            for (BulkItemResponse bulkItemResponse : bulkResponse) {
                if (bulkItemResponse.isFailed()) {
                    BulkItemResponse.Failure failure =
                            bulkItemResponse.getFailure();
                    System.out.println(failure.getMessage());
                }
            }
        }else{
            System.out.println("it is ok");
        }
        for (BulkItemResponse bulkItemResponse : bulkResponse) {
            DocWriteResponse itemResponse = bulkItemResponse.getResponse();

            switch (bulkItemResponse.getOpType()) {
                case INDEX:
                    System.out.println("index");
                case CREATE:
                    IndexResponse indexResponse = (IndexResponse) itemResponse;
                    System.out.println("indexResponse");
                    break;
                case UPDATE:
                    UpdateResponse updateResponse = (UpdateResponse) itemResponse;
                    System.out.println("updateResponse");
                    break;
                case DELETE:
                    DeleteResponse deleteResponse = (DeleteResponse) itemResponse;
                    System.out.println("deleteResponse");
            }
        }
        client.close();
    }




    /**
     * multiGet
     * @throws IOException
     */
    @Test
    public void multiGetTest01() throws IOException {
        MultiGetRequest request = new MultiGetRequest();
        request.add(new MultiGetRequest.Item(
                "posts",
                "doc",
                "2"));
        request.add(new MultiGetRequest.Item("posts", "doc", "1"));
        request.add(new MultiGetRequest.Item("tztest", "user", "1"));

        MultiGetResponse response = client.mget(request, RequestOptions.DEFAULT);
        MultiGetItemResponse[] responses = response.getResponses();
        for(MultiGetItemResponse iteamResponse:responses){
            GetResponse response1 = iteamResponse.getResponse();
            if(response1.isExists())
            System.out.println(response1.getSource());
        }
        client.close();
    }
}
