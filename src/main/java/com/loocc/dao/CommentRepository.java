package com.loocc.dao;


import com.loocc.domain.Comment;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment,Long> {


    List<Comment> findByBlogIdAndParentCommentNull(Long blogId, Sort sort);

    //@Transactional
    //@Query(value = "delete from Comment  c where c.blog.id =?1")
    void deleteByBlogId(Long id);
    }
