package com.dwc.blog.controller;

import com.dwc.blog.entity.Tag;
import com.dwc.blog.entity.Type;
import com.dwc.blog.service.BlogService;
import com.dwc.blog.service.TagService;
import com.dwc.blog.service.TypeService;
import com.dwc.blog.util.Constants;
import com.dwc.blog.vo.BlogQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class TagShowController {
    @Autowired
    private TagService tagService;
    @Autowired
    private BlogService blogService;

    @GetMapping("/tags/{id}")
    public String tags(@PageableDefault(size = 10, sort = {"updateTime"}, direction = Sort.Direction.DESC) Pageable pageable,
                        @PathVariable Long id, Model model) throws Exception {
        List<Tag> tags = tagService.listTagTop(10000); //用10000是为了查询出所有标签的博客，数字足够大就行
        if(id == -1){
            //id为-1，说明此时是从首页跳转过来
            //将第一个Tag的id值复制给id，默认展示第一个Tag对应的博客
            if(tags.size() >= 1){
                id = tags.get(0).getId();
            }else{
                throw new Exception("没有标签");
            }
        }
        model.addAttribute("tags", tags);
        model.addAttribute(Constants.PAGE, blogService.list(id, pageable));
        model.addAttribute("activeTagId", id);
        return "tags";
    }
}
