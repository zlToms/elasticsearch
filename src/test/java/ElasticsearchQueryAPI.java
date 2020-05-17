import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class ElasticsearchQueryAPI {

    public RestHighLevelClient client ;

    @Before
    public void test(){
        client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http")));
    }


    @Test
    public void test01() throws IOException {
        SearchRequest searchRequest = new SearchRequest();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder//.query(QueryBuilders.matchQuery("info","tom1 and tom2 and tom3 and tom4"))
                //.query(QueryBuilders.matchAllQuery())
                //.query(QueryBuilders.multiMatchQuery("tom1 and tom2","name","info"))
                //.query(QueryBuilders.queryStringQuery("name:tom*"))         //模糊匹配
                .query(QueryBuilders.termQuery("name","tom1"))    //精准匹配
                .from(0)
                .size(10)
                .sort("age", SortOrder.ASC)
                //.postFilter(QueryBuilders.rangeQuery("age").from(30).to(35))
                .explain(true);

        searchRequest.indices("test6");                             //指定索引库
        searchRequest.types("user");                                //指定索引表
        searchRequest.source(searchSourceBuilder);
        searchRequest.searchType(SearchType.QUERY_THEN_FETCH);      //指定索引类型

        SearchResponse searchResponse =client.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits searchHits = searchResponse.getHits();
        SearchHit[] hits = searchHits.getHits();

        for(SearchHit hit:hits){
            System.out.println(hit.getSourceAsString());
        }
        client.close();
    }
}
