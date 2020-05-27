package com.loocc.essevice.impl;


import com.fasterxml.jackson.databind.ObjectMapper;

import com.loocc.component.EsMapper;
import com.loocc.domain.EsBlog;
import com.loocc.esdao.BlogEsRepository;
import com.loocc.essevice.BlogEsService;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class BlogEsServiceImpl implements BlogEsService {
    @Autowired
    private ElasticsearchTemplate template;
    @Autowired
    private BlogEsRepository blogEsRepository;
    @Autowired
    private EsMapper esMapper;
    @Autowired
    private TransportClient client;
    public Page<EsBlog> listBlog(String query, Pageable pageable) {
        Page<EsBlog> searchResult = blogEsRepository.findByTitleOrContentOrDescription(query,query,query,pageable);
        ObjectMapper om = new ObjectMapper();
        return searchResult;
        //return null;
    }
    //
    public EsBlog updateEsBlog(Long id){
        Optional<EsBlog> esBlog = blogEsRepository.findById(id);
        EsBlog upes = esBlog.get();
//        upes.setViews(upes.getViews()+1);
//        upes.
        blogEsRepository.save(upes);
        return esBlog.get();

    }

    @Override
    public Page<EsBlog> findByTitleOrContentOrDescription(String title, String content, String description, Pageable pageable) {
        return blogEsRepository.findByTitleOrContentOrDescription(title,content,description,pageable);
    }

    //public List<EsBlog> search(String query) {
    //    HighlightBuilder highlightBuilder = new HighlightBuilder();
    //    //高亮显示规则
    //    highlightBuilder.preTags("<span style='color:red'>");
    //    highlightBuilder.postTags("</span>");
    //    //指定高亮字段
    //    highlightBuilder.field("title");
    //    highlightBuilder.field("content");
    //    highlightBuilder.field("description");
    //    String[] fileds = {"title", "content", "description"};
    //    QueryBuilder matchQuery = QueryBuilders.multiMatchQuery(query, fileds);
    //    //QueryBuilder matchQuery = QueryBuilders.termQuery("title.keyword",query);
    //    //搜索数据
    //    SearchResponse response = client.prepareSearch("myblog")
    //            .setQuery(matchQuery)
    //            .highlighter(highlightBuilder)
    //            .execute().actionGet();
    //
    //    SearchHits searchHits = response.getHits();
    //    System.out.println("记录数-->" + searchHits.getTotalHits());
    //
    //    List<EsBlog> list = new ArrayList<>();
    //
    //    for (SearchHit hit : searchHits) {
    //        EsBlog blog = new EsBlog();
    //        Map<String, Object> entityMap = hit.getSourceAsMap();
    //        System.out.println(hit.getHighlightFields());
    //        //高亮字段
    //        if (!StringUtils.isEmpty(hit.getHighlightFields().get("name"))) {
    //            Text[] text = hit.getHighlightFields().get("name").getFragments();
    //            blog.setTitle(text[0].toString());
    //            //blog.setContent(String.valueOf(entityMap.get("director")));
    //            //blog.setDescription("");
    //        }
    //        if (!StringUtils.isEmpty(hit.getHighlightFields().get("content"))) {
    //            Text[] text = hit.getHighlightFields().get("content").getFragments();
    //            //blog.setTitle(text[0].toString());
    //            //blog.setContent(String.valueOf(entityMap.get("director")));
    //        }
    //        if (!StringUtils.isEmpty(hit.getHighlightFields().get("description"))) {
    //            Text[] text = hit.getHighlightFields().get("description").getFragments();
    //            blog.setContent(text[0].toString());
    //            //blog.setContent(String.valueOf(entityMap.get("name")));
    //        }
    //
    //        //map to object
    //        if (!CollectionUtils.isEmpty(entityMap)) {
    //            if (!StringUtils.isEmpty(entityMap.get("id"))) {
    //                blog.setId(Long.valueOf(String.valueOf(entityMap.get("id"))));
    //            }
    //            if (!StringUtils.isEmpty(entityMap.get("language"))) {
    //                blog.setDescription(String.valueOf(entityMap.get("language")));
    //            }
    //        }
    //        list.add(blog);
    //    }
    //    return list;
    //}
    public Page<EsBlog> highLightQuery(String sortType, String sortField,
                                         String[] searchFields, String keyword,Pageable pageable){

        Sort sort = null;
        if(!StringUtils.isEmpty(sortField)){
            sort = "ASC".equals(sortType) ? Sort.by(Sort.Direction.ASC, sortField) : Sort.by(Sort.Direction.DESC,
                    sortField);
        }
        NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder();
        BoolQueryBuilder bBuilder = QueryBuilders.boolQuery();

        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.preTags("<span style=\"color:red\">").postTags("</span>");//包裹搜索词的标签

        for(String searchField:searchFields){
            highlightBuilder.field(new HighlightBuilder.Field(searchField));//构造高亮显示的Builder
            bBuilder.should(QueryBuilders.termQuery(searchField,keyword));//多字段搜索关系 or
        }
        searchQueryBuilder.withQuery(bBuilder).withHighlightBuilder(highlightBuilder).withPageable(pageable);
        Page<EsBlog> esBlogs = template.queryForPage(searchQueryBuilder.build(), EsBlog.class, esMapper);
        System.out.println(esBlogs.getContent());
        return esBlogs;
    }
//
//
}
