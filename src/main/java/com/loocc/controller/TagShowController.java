package com.loocc.controller;


import com.loocc.domain.Blog;
import com.loocc.domain.Tag;
import com.loocc.service.BlogService;
import com.loocc.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;

@Controller
public class TagShowController {

    @Autowired
    private TagService tagService;

    @Autowired
    private BlogService blogService;

    @GetMapping("/tags/{id}")
    public String tags(@PageableDefault(size = 8, sort = {"updateTime"}, direction = Sort.Direction.DESC) Pageable pageable,
                       @PathVariable Long id, Model model) {
        List<Tag> tags = tagService.listTagTop(10000);//所有标签列表
        if (tags.size() == 0 || tags == null) {//没有标签
            if (tags == null) tags = new ArrayList<>();//为空前端会报错
            model.addAttribute("msg", "这里好像什么都没有~");
            model.addAttribute("tags", tags);
            return "tags";
        }


        if (id == -1) {
            id = tags.get(0).getId();//从顶部点击
        }
        Page<Blog> blogPage = blogService.listBlog(id, pageable, true);//标签对应的博客
        List<Blog> tblog = blogPage.getContent();
        model.addAttribute("total", blogPage.getTotalElements());
        model.addAttribute("activeTagId", id);
        if (tblog.size() == 0 || tblog == null) {
            //标签下为空
            if (tblog == null) tblog = new ArrayList<>();
            model.addAttribute("flag", false);
            model.addAttribute("msg", "该标签下好像没有内容额~");
            model.addAttribute("tags", tags);
            return "tags";
        }


        model.addAttribute("tags", tags);
        model.addAttribute("page", blogService.listBlog(id, pageable, true));
        model.addAttribute("flag", true);
        return "tags";
    }
}
