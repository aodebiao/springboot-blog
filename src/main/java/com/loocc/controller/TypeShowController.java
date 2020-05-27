package com.loocc.controller;

import com.loocc.domain.Blog;
import com.loocc.domain.BlogType;
import com.loocc.service.BlogService;
import com.loocc.service.BlogTypeService;
import com.loocc.vo.BlogQuery;
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
public class TypeShowController {

    @Autowired
    private BlogTypeService typeService;

    @Autowired
    private BlogService blogService;

    @GetMapping("/types/{id}")
    public String types(@PageableDefault(size = 8, sort = {"updateTime"}, direction = Sort.Direction.DESC) Pageable pageable,
                        @PathVariable Long id, Model model) {
        List<BlogType> types = typeService.listTypeTop(10000);//所有分类列表
        if (types == null || types.size() == 0) {
            if (types == null) types = new ArrayList<>();
            model.addAttribute("types", types);
            model.addAttribute("msg", "这里什么都没有额~");
            return "types";
        }
        if (id == -1) {
            //表明从导航栏点进入的
            id = types.get(0).getId();
        }
        Page<Blog> blogPage = blogService.listBlog(id, pageable, false);//该分类下的博客
        List<Blog> blogs = blogPage.getContent();


        model.addAttribute("total", blogPage.getTotalElements());

        model.addAttribute("activeTypeId", id);
        if (blogs.size() == 0 || blogs == null) {
            if (blogs == null) blogs = new ArrayList<>();
            model.addAttribute("flag", false);
            model.addAttribute("msg", "暂无分类信息额~");
            model.addAttribute("types", types);
            return "types";
        }

        //BlogQuery blogQuery = new BlogQuery();
        //blogQuery.setTypeId(id);

        model.addAttribute("types", types);
        model.addAttribute("flag", true);
        model.addAttribute("page", blogPage);

        //根据typeid分页查询
        return "types";
    }
}
