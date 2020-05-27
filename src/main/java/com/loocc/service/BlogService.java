package com.loocc.service;

import com.loocc.domain.Blog;
import com.loocc.vo.BlogQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
public interface BlogService {

    Blog getBlog(Long id);

    Blog getAndConvert(Long id);

    Page<Blog> listBlog(Pageable pageable, BlogQuery blog);

    Page<Blog>findPublishedBlog(Pageable pageable);

    //flag 用来判断是根据tagId查询,还是typeId查询
    Page<Blog> listBlog(Long tagId, Pageable pageable,Boolean flag);


    Page<Blog> listBlog(String query, Pageable pageable);

    List<Blog> listRecommendBlogTop(Integer size);

    Map<String,Page<Blog>> archiveBlog(Pageable pageable);
    //Map<String,List<Blog>> archiveBlog();
    Long countBlog();

    Blog saveBlog(Blog blog);

    Blog updateBlog(Long id, Blog blog);

    void deleteBlog(Long id);

    Blog findById(Long id);
}
