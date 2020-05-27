package com.loocc.service;


import com.loocc.domain.BlogType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

public interface BlogTypeService {
    BlogType saveType(BlogType type);

    BlogType getType(Long id);

    BlogType getTypeByName(String name);

    Page<BlogType> listType(Pageable pageable);

    List<BlogType> listType();

    List<BlogType> listTypeTop(Integer size);

    BlogType updateType(Long id,BlogType type);

    void deleteType(Long id);
    BlogType findById(Long id);
    int saveAll(List<BlogType> blogTypeList, JdbcTemplate jdbcTemplate);
}
