package com.heima.search.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.search.dtos.UserSearchDto;
import com.heima.model.user.beans.ApUser;
import com.heima.search.service.ApUserSearchService;
import com.heima.search.service.ArticleSearchService;
import com.heima.utils.thread.AppThreadLocalUtil;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@Transactional
public class ArticleSearchServiceImpl implements ArticleSearchService {
    @Autowired
    private RestHighLevelClient client;
    @Autowired
    private ApUserSearchService apUserSearchService;

    /**
     * es文章分页检索
     *
     * @return
     */
    @Override
    public ResponseResult search(UserSearchDto dto) {
        //1.检查参数
        if (dto == null || StringUtils.isBlank(dto.getSearchWords())) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        SearchRequest request = new SearchRequest("app_info_article");
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        List<Map> list = new ArrayList<>();
        try {
            //2.设置查询条件
            //关键词查询
            boolQueryBuilder.must(QueryBuilders.queryStringQuery(dto.getSearchWords()).field("title").field("content").defaultOperator(Operator.OR));
            //查询小于minDate的文章
            boolQueryBuilder.filter(QueryBuilders.rangeQuery("publishTime").lt(dto.getMinBehotTime().getTime()));
            request.source()
                    //分页
                    .from(0)
                    .size(dto.getPageSize())
                    //排序
                    .sort("publishTime", SortOrder.DESC)
                    //高亮
                    .highlighter(new HighlightBuilder().field("title").preTags("<font style='color: red; font-size: inherit;'>").postTags("</font>"))
                    //查询条件
                    .query(boolQueryBuilder);
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            //3.结果封装返回
            SearchHit[] hits = response.getHits().getHits();
            for (SearchHit hit : hits) {
                Map map = JSONObject.parseObject(hit.getSourceAsString(), Map.class);
                Map<String, HighlightField> highlightFieldMap = hit.getHighlightFields();
                if (highlightFieldMap != null && !highlightFieldMap.isEmpty()) {
                    HighlightField highlightField = highlightFieldMap.get("title");
                    Text[] texts = highlightField.getFragments();
                    String title = StringUtils.join(texts);
                    //高亮标题
                    map.put("h_title", title);
                } else {
                    //原始标题
                    map.put("h_title", map.get("title"));
                }
                list.add(map);
            }
            //异步调用 保存搜索记录
            ApUser user = AppThreadLocalUtil.getUser();
            if (user != null && user.getId() != null&&dto.getFromIndex()==0) {
                apUserSearchService.insert(dto.getSearchWords(), user.getId());
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return ResponseResult.okResult(list);
    }
}
