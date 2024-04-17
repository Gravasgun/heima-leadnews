package com.heima.search.test;

import com.heima.search.SearchApplication;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@SpringBootTest(classes = SearchApplication.class)
@RunWith(SpringRunner.class)
public class ArticleESTest {

    @Autowired
    private RestHighLevelClient client;

    @Test
    public void deleteDocument() throws IOException {
        SearchRequest request = new SearchRequest("app_info_article");
        request.source().query(QueryBuilders.matchAllQuery()).from(0).size(85);
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        SearchHit[] hits = response.getHits().getHits();
        for (SearchHit hit : hits) {
            client.delete(new DeleteRequest("app_info_article", hit.getId()), RequestOptions.DEFAULT);
        }
    }

}
