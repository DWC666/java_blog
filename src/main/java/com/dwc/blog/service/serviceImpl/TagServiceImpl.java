package com.dwc.blog.service.serviceImpl;

import com.dwc.blog.dao.TagRepository;
import com.dwc.blog.entity.Tag;
import com.dwc.blog.exception.NotFoundException;
import com.dwc.blog.service.TagService;
import com.dwc.blog.util.MyStringUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagServiceImpl implements TagService {
    @Autowired
    private TagRepository repository;

    @Override
    public Tag saveTag(Tag tag) {
        return repository.save(tag);
    }

    @Override
    public Tag getTag(Long id) {
        return repository.getOne(id);
    }

    @Override
    public Page<Tag> listTag(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public List<Tag> listTag() {
        return repository.findAll();
    }

    @Override
    public List<Tag> listTagTop(Integer size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "blogs.size");
        Pageable pageable = PageRequest.of(0, size, sort);
        return repository.findTop(pageable);
    }

    @Override
    public List<Tag> listTag(String ids) {
        return repository.findAllById(MyStringUtil.convertToList(ids));
    }

    @Override
    public Tag updateTag(Long id, Tag tag) {
        Tag t = repository.getOne(id);
        if(t == null){
            throw new NotFoundException("不存在该标签");
        }
        BeanUtils.copyProperties(tag, t);
        return repository.save(t);
    }

    @Override
    public void deleteTag(Long id) {
        repository.deleteById(id);
    }

    @Override
    public Tag getTagByName(String name) {
        return repository.findByName(name);
    }
}
