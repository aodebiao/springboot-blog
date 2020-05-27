package com.loocc.controller.admin;
import com.loocc.domain.BlogType;
import com.loocc.domain.Tag;
import com.loocc.service.BlogTypeService;
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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class BlogTypeController {

    @Autowired
    private BlogTypeService typeService;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @GetMapping("/types")
    @RequiresPermissions("分类列表")
    public String types(@PageableDefault(size = 10, sort = {"id"}, direction = Sort.Direction.DESC)
                                Pageable pageable, Model model) {
        model.addAttribute("page", typeService.listType(pageable));
        return "admin/types";
    }

    @GetMapping("/types/input")
    @RequiresPermissions("新增分类")
    public String input(Model model) {
        model.addAttribute("type", new BlogType());
        return "admin/types-input";
    }

    @GetMapping("/types/{id}/input")
    @RequiresPermissions("修改分类")
    public String editInput(@PathVariable Long id, Model model) {
        model.addAttribute("type", typeService.getType(id));
        return "admin/types-input";
    }


    @PostMapping("/types")
    /**增加的方法**/
    @RequiresPermissions("新增分类")
    public String post(@RequestParam(value = "name") String types, RedirectAttributes attributes, Model model) {
        List<String> typeList = null;
        //批量保存
        if (types.contains(",")) {
            String[] tagArray = types.split(",");
            typeList = Arrays.asList(tagArray);
            if (typeList.size() == 0) {
                return "redirect:/admin/types";
            }
            //过虑(同时和数据库中的比较,已有则不重复添加)去重映射转换
            List<BlogType> list = typeList.stream().filter(x -> !x.trim().equals("") && typeService.getTypeByName(x) == null).distinct().map(BlogType::new).collect(Collectors.toList());
            if (list.size() > 0) {
                typeService.saveAll(list, jdbcTemplate);
            }
        } else {//单个
            if (types.trim().equals("")) {
                return "redirect:/admin/types";
            }
            BlogType type = typeService.getTypeByName(types);
            if (type != null) {
//                result.rejectValue("name","nameError","不能添加重复的分类！");
                return "redirect:/admin/types";
            }
            typeService.saveType(new BlogType(types));
        }
        return "redirect:/admin/types";
    }


    @PostMapping("/types/{id}")
    @RequiresPermissions("修改分类")
    public String editPost(@Valid BlogType type, BindingResult result, @PathVariable Long id,
                           RedirectAttributes attributes, Model model) {
        BlogType type1 = typeService.getTypeByName(type.getName());
        if (type1 != null) {
            model.addAttribute("type", type);
            model.addAttribute("error", "修改失败,类型重复！");
            return "admin/types-input";
        }
        if (result.hasErrors()) {
            model.addAttribute("type", type);
            model.addAttribute("error", "用户名不能为空！");
            return "admin/types-input";
        }
        BlogType t = typeService.updateType(id, type);
        if (t == null) {
            attributes.addFlashAttribute("message", "更新失败");
        } else {
            attributes.addFlashAttribute("message", "更新成功");
        }
        return "redirect:/admin/types";
    }

    @GetMapping("/types/{id}/delete")
    @RequiresPermissions("删除分类")
    public String delete(@PathVariable Long id, RedirectAttributes attributes) {
        typeService.deleteType(id);
        attributes.addFlashAttribute("message", "删除成功");
        return "redirect:/admin/types";
    }


}
