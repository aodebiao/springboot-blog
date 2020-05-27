package com.loocc.controller;


import com.loocc.service.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class ArchiveShowController {

    @Autowired
    private BlogService blogService;

    @GetMapping("/archives")
    public String archives(Model model,@PageableDefault(value = 10,sort="updateTime",direction = Sort.Direction.DESC) Pageable pageable) {
        model.addAttribute("archiveMap", blogService.archiveBlog(pageable));
        model.addAttribute("blogCount", blogService.countBlog());
        return "archives";
    }
}
