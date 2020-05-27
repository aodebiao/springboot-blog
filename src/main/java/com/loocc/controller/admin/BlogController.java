package com.loocc.controller.admin;

import com.loocc.domain.Blog;
import com.loocc.domain.EsBlog;
import com.loocc.domain.User;
import com.loocc.esdao.BlogEsRepository;
import com.loocc.service.BlogService;
import com.loocc.service.BlogTypeService;
import com.loocc.service.TagService;
import com.loocc.util.MyBeanUtils;
import com.loocc.vo.BlogQuery;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.Date;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class BlogController {
    private static final String INPUT = "admin/blogs-input";
    private static final String LIST = "admin/blogs";
    private static final String REDIRECT_LIST = "redirect:/admin/blogs";
    @Autowired
    private BlogService blogService;
    @Autowired
    private BlogTypeService typeService;
    @Autowired
    private TagService tagService;
    @Autowired
    private BlogEsRepository blogEsRepository;

    @GetMapping("/blogs")
    @RequiresPermissions("博客列表")
    public String blogs(@PageableDefault(size = 8, sort = {"updateTime"}, direction = Sort.Direction.DESC) Pageable pageable,
                        BlogQuery blog, Model model) {
        model.addAttribute("types", typeService.listType());
        model.addAttribute("page", blogService.listBlog(pageable, blog));
        return LIST;
    }

    @PostMapping("/blogs/search")//局部刷新表格内容
    @RequiresPermissions("后台搜索")
    public String search(@PageableDefault(size = 8, sort = {"updateTime"}, direction = Sort.Direction.DESC) Pageable pageable,
                         BlogQuery blog, Model model) {
        model.addAttribute("page", blogService.listBlog(pageable, blog));
        return "admin/blogs :: blogList";
    }

    @GetMapping("/blogs/input")
    @RequiresPermissions("进入新增博客编辑页面")
    public String input(Model model) {
        setTypeAndTag(model);
        model.addAttribute("blog", new Blog());
        return INPUT;
    }

    private void setTypeAndTag(Model model) {
        model.addAttribute("types", typeService.listType());
        model.addAttribute("tags", tagService.listTag());
    }


    @GetMapping("/blogs/{id}/input")
    @RequiresPermissions("进入博客编辑页面")
    public String editInput(@PathVariable Long id, Model model) {
        setTypeAndTag(model);
        Blog blog = blogService.getBlog(id);
        blog.init();
        model.addAttribute("blog",blog);
        return INPUT;
    }



    @PostMapping("/blogs")
    @RequiresPermissions("更新或者新增博客")
    public  String post(Blog blog, EsBlog esBlog, RedirectAttributes attributes, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        blog.setUser(currentUser);
        blog.setType(typeService.findById(blog.getType().getId()));
        blog.setTags(tagService.listTag(blog.getTagIds()));
        Blog b;
        if(blog.getId() == null) {//新增
            b =  blogService.saveBlog(blog);//保存到数据库中就有id，方便后面用
            esBlog.setCreateTime(new Date());
            esBlog.setUserName(currentUser.getUsername());
            esBlog.setId(b.getId());
            esBlog.setType(blog.getType().getName());
            esBlog.setViews(0);
            esBlog.setAvatar(currentUser.getAvatar());
            esBlog.setNickName(currentUser.getNickname());
            blogEsRepository.save(esBlog);
        } else {//更新
            b = blogService.updateBlog(blog.getId(), blog);
            Optional<EsBlog> esb = blogEsRepository.findById(blog.getId());
            //复制属性到后者,并忽略掉null属性
            esb.get().setType(b.getType().getName());
            BeanUtils.copyProperties(esBlog,esb.get(),MyBeanUtils.getNullPropertyNames(esBlog));
            blogEsRepository.save(esb.get());
        }

        if (b == null ) {
            attributes.addFlashAttribute("message", "操作失败！");
        } else {
            attributes.addFlashAttribute("message", "操作成功！");
        }
        return REDIRECT_LIST;
    }


    @GetMapping("/blogs/{id}/delete")
    @RequiresPermissions("删除博客")
    public String delete(@PathVariable Long id, RedirectAttributes attributes) {
        blogService.deleteBlog(id);
        blogEsRepository.deleteById(id);
        attributes.addFlashAttribute("message", "删除成功。");
        return REDIRECT_LIST;
    }



}
