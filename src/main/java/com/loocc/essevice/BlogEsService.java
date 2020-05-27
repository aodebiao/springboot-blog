package com.loocc.essevice;


import com.loocc.domain.EsBlog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BlogEsService {
    Page<EsBlog> listBlog(String query, Pageable pageable);
    EsBlog updateEsBlog(Long id);
    Page<EsBlog> findByTitleOrContentOrDescription(String title,String content,String description, Pageable pageable);
    Page<EsBlog> highLightQuery(String sortType, String sortField,
                                String[] searchFields, String keyword,Pageable pageable);
}
