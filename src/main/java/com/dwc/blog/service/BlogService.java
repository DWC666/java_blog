package com.dwc.blog.service;

import com.dwc.blog.entity.Blog;
import com.dwc.blog.vo.BlogQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface BlogService {
    Blog getBlog(Long id);
    Blog getAndConvert(Long id);
    Page<Blog> listBlog(Pageable pageable, BlogQuery blog); //BlogQuery用于封装筛选条件
    Page<Blog> list(Pageable pageable);
    Page<Blog> list(Long tagId, Pageable pageable);
    Page<Blog> list(String query, Pageable pageable);
    List<Blog> listRecommendBlogTop(Integer size);
    Blog saveBlog(Blog blog);
    void deleteBlog(Long id);
    Blog updateBlog(Long id, Blog blog);
    Map<String, List<Blog>> archiveBlog();
    Long countBlog();
}
