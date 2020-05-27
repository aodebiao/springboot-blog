package com.loocc.service.impl;


import com.loocc.NotFoundException;
import com.loocc.dao.BlogRepository;
import com.loocc.dao.CommentRepository;
import com.loocc.domain.Blog;
import com.loocc.domain.BlogType;
import com.loocc.domain.EsBlog;
import com.loocc.esdao.BlogEsRepository;
import com.loocc.service.BlogService;
import com.loocc.util.MarkdownUtils;
import com.loocc.util.MyBeanUtils;
import com.loocc.vo.BlogQuery;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.*;
import java.util.*;

@Service
public class BlogServiceImpl implements BlogService {


    @Autowired
    private BlogRepository blogRepository;
    
    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private BlogEsRepository blogEsRepository;

    @Override
    public Blog getBlog(Long id) {
        return blogRepository.getOne(id);
    }

    @Transactional
    @Override
    public Blog getAndConvert(Long id) {
       Blog blog = blogRepository.getOne(id);
        if (blog == null) {
            throw new NotFoundException("该博客不存在");
        }
        Blog b = new Blog();
        BeanUtils.copyProperties(blog, b);
        String content = b.getContent();
        //把markdown格式内型的文体转化成html
        b.setContent(MarkdownUtils.markdownToHtmlExtensions(content));
        blogRepository.updateViews(id);

        //更新es中的浏览次数
        Optional<EsBlog> blogOptional = blogEsRepository.findById(id);
        blogOptional.get().setViews(blogOptional.get().getViews() + 1);
        blogEsRepository.save(blogOptional.get());
        return b;
    }


    @Override
    public Page<Blog> listBlog(Pageable pageable, BlogQuery blog) {
        return blogRepository.findAll((root, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (!"".equals(blog.getTitle()) && blog.getTitle() != null) {
                predicates.add(cb.like(root.<String>get("title"), "%" + blog.getTitle() + "%"));
            }
            if (blog.getTypeId() != null) {
                predicates.add(cb.equal(root.<BlogType>get("type").get("id"), blog.getTypeId()));
            }
            if (blog.isRecommend()) {
                predicates.add(cb.equal(root.<Boolean>get("recommend"), blog.isRecommend()));
            }
            cq.where(predicates.toArray(new Predicate[predicates.size()]));
            return null;
        }, pageable);
    }



    @Override
    public Page<Blog> findPublishedBlog(Pageable pageable) {
        return blogRepository.findPublishedBlog(pageable);
    }

    @Override
    public Page<Blog> listBlog(Long id, Pageable pageable,Boolean flag) {
        // true ---> tag,false ---> type
        if(flag){
            return blogRepository.findAll((root, cq, cb) -> {
                Join join = root.join("tags");//关联查询
                return cb.equal(join.get("id"), id);
            }, pageable);
        }else {
            return blogRepository.findAll((root, cq, cb) -> {
                Join join = root.join("type");//关联查询
                return cb.equal(join.get("id"), id);
            }, pageable);
        }
    }

    @Override
    public Page<Blog> listBlog(String query, Pageable pageable) {
        return blogRepository.findByQuery(query, pageable);
    }

    @Override
    public List<Blog> listRecommendBlogTop(Integer size) {
        //ArrayList<String> list = new ArrayList<>();
        //list.add("updateTime");
        Sort sort = Sort.by("updateTime");
        Pageable pageable = PageRequest.of(0, size, sort);
        return blogRepository.findTop(pageable);
    }

    @Override
    public Map<String, Page<Blog>> archiveBlog(Pageable pageable) {//按年份归档
        List<String> years = blogRepository.findGroupYear();
        Map<String, Page<Blog>> map = new HashMap<>();
        for (String year : years) {
            map.put(year, blogRepository.groupByYear(year,pageable));
        }
        return map;
    }

    @Override
    public Long countBlog() {
        return blogRepository.count();
    }


    @Transactional
    @Override
    public Blog saveBlog(Blog blog) {
        if (blog.getId() == null) {
            blog.setCreateTime(new Date());
            blog.setUpdateTime(new Date());
            blog.setViews(0);
        } else {
            blog.setUpdateTime(new Date());
        }
        return blogRepository.save(blog);
    }

    @Transactional
    @Override
    public Blog updateBlog(Long id, Blog blog) {
        Blog b = blogRepository.getOne(id);
        if (b == null) {
            throw new NotFoundException("该博客不存在");
        }
        //解决更新博客的时候，由于前端没有传一些浏览次数，导致更新后数据库中的对应字段为0
        BeanUtils.copyProperties(blog, b, MyBeanUtils.getNullPropertyNames(blog));
        b.setUpdateTime(new Date());
        return blogRepository.save(b);
    }

    @Transactional
    @Override
    public void deleteBlog(Long id) {
        commentRepository.deleteByBlogId(id);
        blogRepository.deleteById(id);
    }

    @Override
    public Blog findById(Long id) {
        Optional<Blog> blogOptional = blogRepository.findById(id);
        return blogOptional.get();
    }
}
