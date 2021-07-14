package com.dwc.blog.controller;

import com.dwc.blog.entity.Comment;
import com.dwc.blog.entity.User;
import com.dwc.blog.service.BlogService;
import com.dwc.blog.service.CommentService;
import com.dwc.blog.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;


@Controller
public class CommentController {
    @Autowired
    private CommentService commentService;

    @Autowired
    private BlogService blogService;

    //将配置文件中的值注入进来
    @Value("${comment.avatar}")
    private String avatar;

    /**
     * 返回博客的评论列表
     * @param blogId
     * @param model
     * @return
     */
    @GetMapping("/comments/{blogId}")
    public String comments(@PathVariable Long blogId, Model model){
        model.addAttribute("comments", commentService.listCommentByBlogId(blogId));
        return "blog :: commentList"; //局部刷新
    }

    /**
     * 提交评论
     * @return
     */
    @PostMapping("/comments")
    public String comment(Comment comment, HttpSession session){
        Long blogId = comment.getBlog().getId();
        comment.setBlog(blogService.getBlog(blogId));
        User user = (User) session.getAttribute(Constants.LOGIN_USER);
        if(user != null){ //说明当前用户是管理员
            comment.setAvatar(user.getAvatar());
            comment.setAdminComment(true);
        }else{
            comment.setAvatar(avatar);
        }
        commentService.saveComment(comment);
        return "redirect:/comments/" + comment.getBlog().getId();
    }
}
