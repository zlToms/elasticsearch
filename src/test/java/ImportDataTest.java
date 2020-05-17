import elasticsearch.test.DataUtils;
import org.apache.http.HttpHost;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.bulk.*;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

import static org.elasticsearch.client.RequestOptions.DEFAULT;

public class ImportDataTest {
    public RestHighLevelClient client ;

    @Before
    public void test(){
        client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http"))
                .setMaxRetryTimeoutMillis(50000)
        );
    }


    /**
     * Bulk
     * @throws IOException
     */
    @Test
    public void bulkTest01() throws IOException {
        client.getLowLevelClient();

        BulkRequest request = new BulkRequest();
        request.timeout(TimeValue.timeValueSeconds(1));
        request.timeout("1000000s");
        DataUtils dataUtils= new DataUtils();
        String sqlRow = "SELECT count(*) FROM t_user ";
        int rows= dataUtils.getRows(sqlRow);


        List<String> keyList= new ArrayList<>();
        keyList.add("id"+":"+"1");
        keyList.add("userName"+":"+"2");
        keyList.add("realName"+":"+"2");
        keyList.add("sex"+":"+"2");
        keyList.add("mobile"+":"+"2");
        keyList.add("email"+":"+"2");
        keyList.add("note"+":"+"2");
        if(rows<100000){
            String sql = "SELECT * FROM t_user ";
            List<Map<String, String>> maps = dataUtils.queryData(sql,keyList);
            for(Map<String, String> map:maps){
                request.add(new IndexRequest("posts", "doc", map.get("id"))
                        .source(map));
            }
            BulkResponse bulkResponse = client.bulk(request, DEFAULT);
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
        }else{
            int count = rows/100000;
            for(int i =0;i<=count;i++){
                String sql = "SELECT * FROM t_user limit "+ i*100000+","+(i+1)*100000;

                List<Map<String, String>> maps = dataUtils.queryData(sql,keyList);
                for(Map<String, String> map:maps){
                    request.add(new IndexRequest("posts", "doc", map.get("id"))
                            .source(map));

                }
                BulkResponse bulkResponse = client.bulk(request, DEFAULT);
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
            }
        }
        client.close();
    }

    @Test
    public void insetTuser(){
        DataUtils dataUtils= new DataUtils();
        dataUtils.bathInsert();
    }



    private static BulkProcessor getBulkProcessor(RestHighLevelClient client) {

        BulkProcessor bulkProcessor = null;
        try {

            BulkProcessor.Listener listener = new BulkProcessor.Listener() {
                @Override
                public void beforeBulk(long executionId, BulkRequest request) {

                }

                @Override
                public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {

                }

                @Override
                public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
                    System.out.println("Bulk is unsuccess : " + failure + ", executionId: " + executionId);
                }
            };

            BiConsumer<BulkRequest, ActionListener<BulkResponse>> bulkConsumer = (request, bulkListener) -> client
                    .bulkAsync(request, DEFAULT, bulkListener);

            //	bulkProcessor = BulkProcessor.builder(bulkConsumer, listener).build();
            BulkProcessor.Builder builder = BulkProcessor.builder(bulkConsumer, listener);
            builder.setBulkActions(5000);
            builder.setBulkSize(new ByteSizeValue(100L, ByteSizeUnit.MB));
            builder.setConcurrentRequests(10);
            builder.setFlushInterval(TimeValue.timeValueSeconds(100L));
            builder.setBackoffPolicy(BackoffPolicy.constantBackoff(TimeValue.timeValueSeconds(1L), 3));
            // 注意点：在这里感觉有点坑，官网样例并没有这一步，而笔者因一时粗心也没注意，在调试时注意看才发现，上面对builder设置的属性没有生效
            bulkProcessor = builder.build();

        } catch (Exception e) {
            e.printStackTrace();
            try {
                bulkProcessor.awaitClose(100L, TimeUnit.SECONDS);
                client.close();
            } catch (Exception e1) {

            }
        }
        return bulkProcessor;
    }


    /**
     * Bulk
     * @throws IOException
     */
    @Test
    public void bulkTest02() throws IOException {

        BulkRequest request = new BulkRequest();
        DataUtils dataUtils= new DataUtils();
        String sqlRow = "SELECT count(*) FROM t_user ";
        int rows= dataUtils.getRows(sqlRow);


        List<String> keyList= new ArrayList<>();
        keyList.add("id"+":"+"1");
        keyList.add("userName"+":"+"2");
        keyList.add("realName"+":"+"2");
        keyList.add("sex"+":"+"2");
        keyList.add("mobile"+":"+"2");
        keyList.add("email"+":"+"2");
        keyList.add("note"+":"+"2");

        BulkProcessor bulkProcessor = getBulkProcessor(client);
        if(rows<10000){
            String sql = "SELECT * FROM t_user ";
            List<Map<String, String>> maps = dataUtils.queryData(sql,keyList);
            for(Map<String, String> map:maps){
                bulkProcessor.add(new IndexRequest("posts", "doc", map.get("id"))
                        .source(map));
            }
            //BulkResponse bulkResponse = client.bulk(request, DEFAULT);
            bulkProcessor.flush();

        }else{
            int count = rows/100000;
            for(int i =0;i<=count;i++){
                String sql = "SELECT * FROM t_user limit "+ i*100000+","+(i+1)*100000;

                List<Map<String, String>> maps = dataUtils.queryData(sql,keyList);
                for(Map<String, String> map:maps){
                    bulkProcessor.add(new IndexRequest("posts", "doc", map.get("id"))
                            .source(map));

                }
                bulkProcessor.flush();
            }
        }
        client.close();
    }

}
