package com.dwc.blog.service.serviceImpl;

import com.dwc.blog.dao.BlogRepository;
import com.dwc.blog.entity.Blog;
import com.dwc.blog.entity.Type;
import com.dwc.blog.exception.NotFoundException;
import com.dwc.blog.service.BlogService;
import com.dwc.blog.util.MarkdownUtils;
import com.dwc.blog.vo.BlogQuery;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.*;
import java.util.*;

@Service
public class BlogServiceImpl implements BlogService {
    @Autowired
    private BlogRepository repository;

    @Override
    public Blog getBlog(Long id) {
        return repository.getOne(id);
    }

    //将数据库中的Markdown格式的博客转换为html格式
    @Transactional
    @Override
    public Blog getAndConvert(Long id) {
        Blog blog = repository.getOne(id);
        if(blog == null){
            throw new NotFoundException("该博客不存在！");
        }
        Blog copyBlog = new Blog();
        BeanUtils.copyProperties(blog, copyBlog);
        String content = copyBlog.getContent();
        copyBlog.setContent(MarkdownUtils.markdownToHtmlExtensions(content));
        repository.updateViews(id);
        return copyBlog;
    }

    //根据条件查询博客列表
    @Override
    public Page<Blog> listBlog(Pageable pageable, BlogQuery blog) {
        return repository.findAll(new Specification<Blog>() {
            @Override
            public Predicate toPredicate(Root<Blog> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
                List<Predicate> predicateList = new ArrayList<>();
                if(!StringUtils.isEmpty(blog.getTitle())){
                    predicateList.add(cb.like(root.get("title"), "%"+blog.getTitle()+"%"));
                }

                if(blog.getTypeId() != null){
                    predicateList.add(cb.equal(root.<Type>get("type").get("id"), blog.getTypeId()));
                }

                if(blog.isRecommend()){
                    predicateList.add(cb.equal(root.<Boolean>get("recommend"), blog.isRecommend()));
                }
                cq.where(predicateList.toArray(new Predicate[predicateList.size()]));
                return null;
            }
        }, pageable);
    }

    @Override
    public Page<Blog> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    /**
     * 根据tagId查询博客
     * @param tagId
     * @param pageable
     * @return
     */
    @Override
    public Page<Blog> list(Long tagId, Pageable pageable) {
        return repository.findAll(new Specification<Blog>() {
            @Override
            public Predicate toPredicate(Root<Blog> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
                Join join = root.join("tags");
                return cb.equal(join.get("id"), tagId);
            }
        }, pageable);
    }

    //模糊查询
    @Override
    public Page<Blog> list(String query, Pageable pageable) {
        return repository.findByQuery(query, pageable);
    }

    @Override
    public List<Blog> listRecommendBlogTop(Integer size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "updateTime");
        Pageable pageable = PageRequest.of(0, size, sort);
        return repository.findTop(pageable);
    }

    @Transactional
    @Override
    public Blog saveBlog(Blog blog) {
        if(blog.getId() == null){
            //id为null说明该博客是新增的
            blog.setCreateTime(new Date());
            blog.setUpdateTime(new Date());
            blog.setViews(0);
        }else{
            //博客是重新编辑的
            blog.setUpdateTime(new Date());
        }

        return repository.save(blog);
    }

    @Transactional
    @Override
    public void deleteBlog(Long id) {
        repository.deleteById(id);
    }

    @Transactional
    @Override
    public Blog updateBlog(Long id, Blog blog) {
        Blog b = repository.getOne(id);
        if(b == null){
            throw new NotFoundException("该博客不存在！");
        }
        BeanUtils.copyProperties(blog, b);
        b.setUpdateTime(new Date());
        return repository.save(b);
    }

    /**
     * 博客按年份归档
     * @return
     */
    @Override
    public Map<String, List<Blog>> archiveBlog() {
        List<String> years = repository.findGroupYear();
        Map<String, List<Blog>> map = new HashMap<>();
        for(String year : years){
            map.put(year, repository.findByYear(year));
        }
        return map;
    }

    @Override
    public Long countBlog() {
        return repository.count();
    }
}
