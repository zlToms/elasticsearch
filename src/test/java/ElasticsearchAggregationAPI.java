import org.apache.http.HttpHost;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetItemResponse;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.sum.Sum;
import org.elasticsearch.search.aggregations.metrics.sum.SumAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ElasticsearchAggregationAPI {

    public RestHighLevelClient client ;

    @Before
    public void test(){
        client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http")));
    }

    /**
     * 按年龄分组统计
     */
    @Test
    public  void test01() throws IOException {
        SearchRequest searchRequest = new SearchRequest();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //按年龄分组聚合统计
        TermsAggregationBuilder aggregation =AggregationBuilders.terms("by_age").field("age");
        searchSourceBuilder.query(QueryBuilders.matchAllQuery())
                            .aggregation(aggregation);
        searchRequest.indices("test6")
                     .types("user")
                     .source(searchSourceBuilder);

        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        //获取分组集合后的信息
        Terms terms=searchResponse.getAggregations().get("by_age");
        for(Terms.Bucket term :terms.getBuckets()){
            Object key = term.getKey();
            long  doCount =term.getDocCount();
            System.out.println(key +":"+doCount);
        }

        client.close();
    }




    /**
     * 分组统计学员总成绩
     */
    @Test
    public  void test02() throws IOException {
        SearchRequest searchRequest = new SearchRequest();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //按年龄分组聚合统计
        TermsAggregationBuilder nameAggregation =AggregationBuilders.terms("by_name").field("name.keyword");
        //统计总分
        SumAggregationBuilder scoreAggregation=AggregationBuilders.sum("by_score").field("score");
        //总分聚合作为名字集合的子聚合
        nameAggregation.subAggregation(scoreAggregation);
        searchSourceBuilder.query(QueryBuilders.matchAllQuery())
                .aggregation(nameAggregation);
        searchRequest.indices("test7")
                .types("user")
                .source(searchSourceBuilder);

        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        //获取分组集合后的信息
        Terms terms=searchResponse.getAggregations().get("by_name");
        for(Terms.Bucket term :terms.getBuckets()){
            Sum sum = term.getAggregations().get("by_score");
            System.out.println(term.getKey()+":"+sum.getValue());
        }
        client.close();
    }



    /**
     * 支持多索引多类型查询
     */
    @Test
    public  void test03() throws IOException {
        SearchRequest searchRequest = new SearchRequest();

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery())
                            .from(0)
                            .size(50);

        searchRequest.indices("test7","test6")  //也支持通配符
                     .types("user")       //注意支持多类型不支持通配符
                     .source(searchSourceBuilder);

        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits searchHits = searchResponse.getHits();
        SearchHit[] hits = searchHits.getHits();
        for(SearchHit hit:hits){
            System.out.println(hit.getSourceAsString());
        }
        client.close();
    }




    /**
     * 极速查询插入测试数据
     */
    @Test
    public  void test04() throws IOException {
        Map<String,Object> jsonMap = new HashMap<>();
        jsonMap.put("phone","15365257854");
        jsonMap.put("name","zhangsan");
        jsonMap.put("sex","male");
        jsonMap.put("age",20);
        //jsonMap.put("message","insert data for quickSearching");

        Map<String,Object> jsonMap2 = new HashMap<>();
        jsonMap2.put("phone","15245257854");
        jsonMap2.put("name","tz");
        jsonMap2.put("sex","male");
        jsonMap2.put("age",25);

        Map<String,Object> jsonMap3 = new HashMap<>();
        jsonMap3.put("phone","18165257854");
        jsonMap3.put("name","lisi");
        jsonMap3.put("sex","male");
        jsonMap3.put("age",38);

        BulkRequest request = new BulkRequest();
        request.add(new IndexRequest("test8","user").routing(jsonMap.get("phone").toString().substring(0,3)).source(jsonMap))
                .add(new IndexRequest("test8","user").routing(jsonMap2.get("phone").toString().substring(0,3)).source(jsonMap2))
                .add(new IndexRequest("test8","user").routing(jsonMap3.get("phone").toString().substring(0,3)).source(jsonMap3));

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

        client.close();
    }




    /**
     * 极速查询 通过路由实现极速查询
     */
    @Test
    public  void test05() throws IOException {
        SearchRequest searchRequest = new SearchRequest();

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery())
                .from(0)
                .size(50);

        searchRequest.indices("test8")    //也支持通配符
                .types("user")            //注意支持多类型不支持通配符
                .source(searchSourceBuilder)
                .routing("18165257854".substring(0,3));  //极速查询配置相同的路由规则

        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits searchHits = searchResponse.getHits();
        SearchHit[] hits = searchHits.getHits();
        for(SearchHit hit:hits){
            System.out.println(hit.getSourceAsString());
        }
        client.close();
    }
}
