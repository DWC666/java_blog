package com.dwc.blog.dao;

import com.dwc.blog.entity.Comment;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    //根据博客id查找父评论id为null的评论
    List<Comment> findByBlogIdAndParentCommentNull(Long blogId, Sort sort);
}
