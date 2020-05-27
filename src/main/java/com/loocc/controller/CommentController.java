package com.loocc.controller;



import com.loocc.domain.Blog;
import com.loocc.domain.Comment;
import com.loocc.domain.User;
import com.loocc.service.BlogService;
import com.loocc.service.CommentService;
import com.loocc.service.UserService;
import com.loocc.util.Const;
import com.loocc.util.VerifyCode;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

import java.util.List;


@Controller
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private BlogService blogService;

    @Autowired
    private UserService userService;

    @GetMapping("/comments/{blogId}")
    public String comments(@PathVariable Long blogId, Model model) {
        List<Comment> comments = commentService.listCommentByBlogId(blogId);
        model.addAttribute("comments", comments);
        if (comments == null || comments.size() == 0) {
            model.addAttribute("comments", null);
        }
        return "blog :: commentList";
    }


    @RequiresAuthentication
    @PostMapping("/comments")
    public String post(Comment comment, @RequestParam("blog.id") Long bid, Model model, String verifyCode) {
        String vcode = (String)SecurityUtils.getSubject().getSession().getAttribute("verifyCode");
        String code = (vcode).toLowerCase();
        List<Comment> comments = null;
        model.addAttribute("message",null);
        //comments = commentService.listCommentByBlogId(bid);//这里不注释会出现子评论评论后需要刷新页面才会显示。
        model.addAttribute("comments", comments);
        if(!code.trim().equals(verifyCode.trim().toLowerCase())){
            //return " redirect:/comments/" + bid;
            comments = commentService.listCommentByBlogId(bid);
            model.addAttribute("message","验证码输入错误！");
            return "blog :: commentList";
        }
        Subject subject = SecurityUtils.getSubject();
        String username = (String) subject.getPrincipal();

        User user = userService.findByUserName(username);
        Blog blog = blogService.findById(bid);

        if (!user.isStatus() || !blog.isCommentabled()) {
            comments = commentService.listCommentByBlogId(bid);
            model.addAttribute("message","该博客禁止评论或者你的账号状态异常！");
            return "blog :: commentList";
        }
        comment.setBlog(blogService.getBlog(bid));
        Long userId = comment.getUser().getId();

        comment.setUser(userService.findById(userId));
        //从session中获取用户信息，如有则是管理员，把头像和标记设置为相应的值
        if (subject.hasRole("admin")) {
            //comment.setAvatar(user.getAvatar());
            comment.setAdminComment(true);
            //可以在此设置前端页面显示回复的nickname
        }
        model.addAttribute("message",null);
        commentService.saveComment(comment);
        comments = commentService.listCommentByBlogId(bid);
        System.out.println(comments);
        model.addAttribute("comments", comments);

        //return "redirect:/comments/" + bid;
        return "blog :: commentList";
    }


}
