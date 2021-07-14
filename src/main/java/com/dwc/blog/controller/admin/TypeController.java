package com.dwc.blog.controller.admin;

import com.dwc.blog.entity.Type;
import com.dwc.blog.service.TypeService;
import com.dwc.blog.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.BindResult;
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
@RequestMapping("/admin/types")
public class TypeController {

    @Autowired
    private TypeService typeService;

    @GetMapping("/list")
    public String types(@PageableDefault(size = 5, sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable, Model model){
        model.addAttribute(Constants.PAGE, typeService.listType(pageable));
        return "/admin/types";
    }

    @GetMapping("/input")
    public String toInputPage(Model model){
        model.addAttribute("type", new Type());
        return "/admin/types-input";
    }

    @GetMapping("/input/{id}")
    public String toEditType(@PathVariable Long id, Model model){
        model.addAttribute("type", typeService.getType(id));
        return "/admin/types-input";
    }

    @GetMapping("/delete/{id}")
    public String deleteType(@PathVariable Long id, RedirectAttributes attributes){
        typeService.deleteType(id);
        attributes.addFlashAttribute(Constants.MESSAGE, "删除成功");
        return "redirect:/admin/types/list";
    }

    @PostMapping("/addType")
    public String addType(@Valid Type type, BindingResult result, RedirectAttributes attributes){
        Type t1 = typeService.getTypeByName(type.getName());
        if(t1 != null){
            result.rejectValue("name", "nameError", "该分类已存在，不能添加相同的分类");
        }
        if(result.hasErrors()){
            return "/admin/types-input";
        }

        Type t2 = typeService.saveType(type);
        if(t2 == null){
            attributes.addFlashAttribute(Constants.MESSAGE, "添加失败");
        }else{
            attributes.addFlashAttribute(Constants.MESSAGE, "添加成功");
        }
        return "redirect:/admin/types/list";
    }

    @PostMapping("/addType/{id}")
    public String editType(@Valid Type type, BindingResult result, @PathVariable Long id, RedirectAttributes attributes){
        Type t1 = typeService.getTypeByName(type.getName());
        if(t1 != null){
            result.rejectValue("name", "nameError", "该分类已存在，不能添加相同的分类");
        }
        if(result.hasErrors()){
            return "/admin/types-input";
        }

        Type t2 = typeService.updateType(id, type);
        if(t2 == null){
            attributes.addFlashAttribute(Constants.MESSAGE, "更新失败");
        }else{
            attributes.addFlashAttribute(Constants.MESSAGE, "更新成功");
        }
        return "redirect:/admin/types/list";
    }
}
