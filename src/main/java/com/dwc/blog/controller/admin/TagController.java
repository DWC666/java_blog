package com.dwc.blog.controller.admin;

import com.dwc.blog.entity.Tag;
import com.dwc.blog.entity.Type;
import com.dwc.blog.service.TagService;
import com.dwc.blog.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequestMapping("/admin/tags/")
public class TagController {
    @Autowired
    private TagService tagService;

    @GetMapping("/list")
    public String listTags(@PageableDefault(size = 5, sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable, Model model){
        model.addAttribute(Constants.PAGE, tagService.listTag(pageable));
        return "/admin/tags";
    }

    @GetMapping("/input")
    public String toInputPage(Model model){
        model.addAttribute("tag", new Tag());
        return "admin/tags-input";
    }

    @GetMapping("/input/{id}")
    public String toEditPage(@PathVariable Long id, Model model){
        Tag t = tagService.getTag(id);
        model.addAttribute("tag", t);
        return "admin/tags-input";
    }

    @PostMapping("/add")
    public String addTag(@Valid Tag tag, BindingResult result, RedirectAttributes attributes){
        Tag t = tagService.getTagByName(tag.getName());
        if(t != null){
            result.rejectValue("name", "nameError", "该标签已存在，不能添加相同的标签");
        }
        if(result.hasErrors()){
            return "/admin/tags-input";
        }

        Tag t2 = tagService.saveTag(tag);
        if(t2 == null){
            attributes.addFlashAttribute(Constants.MESSAGE, "添加失败");
        }else{
            attributes.addFlashAttribute(Constants.MESSAGE, "添加成功");
        }
        return "redirect:/admin/tags/list";
    }

    @PostMapping("/add/{id}")
    public String addTag(@Valid Tag tag, BindingResult result, @PathVariable Long id, RedirectAttributes attributes){
        Tag t = tagService.getTagByName(tag.getName());
        if(t != null){
            result.rejectValue("name", "nameError", "该标签已存在，不能添加相同的标签");
        }
        if(result.hasErrors()){
            return "/admin/tags-input";
        }

        Tag t2 = tagService.updateTag(id, tag);
        if(t2 == null){
            attributes.addFlashAttribute(Constants.MESSAGE, "修改失败");
        }else{
            attributes.addFlashAttribute(Constants.MESSAGE, "修改成功");
        }
        return "redirect:/admin/tags/list";
    }

    @GetMapping("/delete/{id}")
    public String deleteTag(@PathVariable Long id, RedirectAttributes attributes){
        tagService.deleteTag(id);
        attributes.addFlashAttribute(Constants.MESSAGE, "删除成功");
        return "redirect:/admin/tags/list";
    }
}
