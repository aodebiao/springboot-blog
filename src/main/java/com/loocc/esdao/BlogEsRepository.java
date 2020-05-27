package com.loocc.esdao;


import com.loocc.domain.EsBlog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface BlogEsRepository extends ElasticsearchRepository<EsBlog,Long> {
    Page<EsBlog> findByTitleOrContentOrDescription(String title,String content,String description, Pageable pageable);

}
