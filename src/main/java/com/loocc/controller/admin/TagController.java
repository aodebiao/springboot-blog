package com.loocc.controller.admin;


import com.loocc.domain.Tag;
import com.loocc.service.TagService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class TagController {

    @Autowired
    private TagService tagService;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/tags")
    @RequiresPermissions("标签列表")
    public String tags(@PageableDefault(size = 10, sort = {"id"}, direction = Sort.Direction.DESC)
                               Pageable pageable, Model model) {
        model.addAttribute("page", tagService.listTag(pageable));
        return "admin/tags";
    }

    @GetMapping("/tags/input")
    @RequiresPermissions("新增标签")
    public String input(Model model) {
        model.addAttribute("tag", new Tag());
        return "admin/tags-input";
    }

    @GetMapping("/tags/{id}/input")
    @RequiresPermissions("编辑标签")
    public String editInput(@PathVariable Long id, Model model) {
        model.addAttribute("tag", tagService.getTag(id));
        return "admin/tags-input";
    }


    @PostMapping("/tags")
    @RequiresPermissions("新增标签")
    public String post(@RequestParam(value = "name") String tags, RedirectAttributes attributes) {
        List<String> tagList = null;
        //批量保存
        if (tags.contains(",")) {
            String[] tagArray = tags.split(",");
            tagList = Arrays.asList(tagArray);
            if (tagList.size() == 0) {
                return "redirect:/admin/tags";
            }
            //过虑(同时和数据库中的比较,已有则不重复添加)去重映射转换
            List<Tag> list = tagList.stream().filter(x -> !x.trim().equals("") && tagService.getTagByName(x) == null).distinct().map(Tag::new).collect(Collectors.toList());
            if (list.size() > 0) {
                tagService.saveAll(list, jdbcTemplate);
            }
        } else {//单个
            if (tags.trim().equals("")) {
                return "redirect:/admin/tags";
            }
            Tag tag = tagService.getTagByName(tags);
            if (tag != null) {
//                result.rejectValue("name","nameError","不能添加重复的分类！");
                return "redirect:/admin/tags";
            }
            tagService.saveTag(new Tag(tags));
        }
        return "redirect:/admin/tags";


        //Tag tag1 = tagService.getTagByName(tag.getName());
        //if (tag1 != null) {
        //    result.rejectValue("name","nameError","不能添加重复的分类！");
        //}
        //if (result.hasErrors()) {
        //    return "admin/tags-input";
        //}
        //Tag t = tagService.saveTag(tag);
        //if (t == null ) {
        //    attributes.addFlashAttribute("message", "新增失败");
        //} else {
        //    attributes.addFlashAttribute("message", "新增成功");
        //}
    }


    @PostMapping("/tags/{id}")
    @RequiresPermissions("修改标签")
    public String editPost(@Valid Tag tag, BindingResult result, @PathVariable Long id, RedirectAttributes attributes) {
        Tag tag1 = tagService.getTagByName(tag.getName());
        if (tag1 != null) {
            result.rejectValue("name", "nameError", "不能添加重复的分类！");
        }
        if (result.hasErrors()) {
            return "admin/tags-input";
        }
        Tag t = tagService.updateTag(id, tag);
        if (t == null) {
            attributes.addFlashAttribute("message", "更新失败");
        } else {
            attributes.addFlashAttribute("message", "更新成功");
        }
        return "redirect:/admin/tags";
    }

    @GetMapping("/tags/{id}/delete")
    @RequiresPermissions("删除标签")
    public String delete(@PathVariable Long id, RedirectAttributes attributes) {
        tagService.deleteTag(id);
        attributes.addFlashAttribute("message", "删除成功");
        return "redirect:/admin/tags";
    }


}
