package com.dwc.blog.controller.admin;

import com.dwc.blog.entity.Blog;
import com.dwc.blog.entity.User;
import com.dwc.blog.service.BlogService;
import com.dwc.blog.service.TagService;
import com.dwc.blog.service.TypeService;
import com.dwc.blog.util.Constants;
import com.dwc.blog.vo.BlogQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin/blogs")
public class BlogController {
    private static final String INPUT = "admin/blogs-input";
    private static final String LIST = "admin/blogs";
    private static final String REDIRECT_LIST = "redirect:/admin/blogs/list";

    @Autowired
    private BlogService blogService;

    @Autowired
    private TypeService typeService;

    @Autowired
    private TagService tagService;

    @GetMapping("/list")
    public String blogs(@PageableDefault(size = 5, sort = {"updateTime"}, direction = Sort.Direction.DESC)Pageable pageable,
                        BlogQuery blog, Model model){
        Page<Blog> page = blogService.listBlog(pageable, blog);
        model.addAttribute(Constants.PAGE, page);
        model.addAttribute("types", typeService.listType());
        return LIST;
    }

    //返回片段中的博客列表
    @PostMapping("/list/search")
    public String search(@PageableDefault(size = 5, sort = {"updateTime"}, direction = Sort.Direction.DESC)Pageable pageable,
                        BlogQuery blog, Model model){
        Page<Blog> page = blogService.listBlog(pageable, blog);
        model.addAttribute(Constants.PAGE, page);
        return "admin/blogs :: blogList";
    }

    @GetMapping("/input")
    public String input(Model model){
        model.addAttribute("blog", new Blog());
        model.addAttribute("types", typeService.listType());
        model.addAttribute("tags", tagService.listTag());
        return INPUT;
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model){
        Blog b = blogService.getBlog(id);
        b.init();
        model.addAttribute("blog", b);
        model.addAttribute("types", typeService.listType());
        model.addAttribute("tags", tagService.listTag());
        return INPUT;
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes attributes){
        blogService.deleteBlog(id);
        attributes.addFlashAttribute(Constants.MESSAGE, "删除成功");
        return REDIRECT_LIST;
    }

    @PostMapping("/submit")
    public String newOrEdit(Blog blog, RedirectAttributes attributes, HttpSession session){
        blog.setUser((User) session.getAttribute(Constants.LOGIN_USER));
        blog.setType(typeService.getType(blog.getType().getId()));
        blog.setTags(tagService.listTag(blog.getTagIds()));

        Blog b = blogService.saveBlog(blog);

        if(b == null){
            attributes.addFlashAttribute(Constants.MESSAGE, "提交失败");
        }else{
            attributes.addFlashAttribute(Constants.MESSAGE, "提交成功");
        }
        return REDIRECT_LIST;
    }
}
