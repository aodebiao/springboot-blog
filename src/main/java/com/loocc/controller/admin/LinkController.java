package com.loocc.controller.admin;

import com.loocc.domain.Link;
import com.loocc.service.LinkService;
import com.loocc.util.StringUtil;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.websocket.server.PathParam;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Controller
@RequestMapping("/admin/link")
public class LinkController {
    @Autowired
    private LinkService linkService;
    @RequestMapping("/list")
    @RequiresPermissions("友链列表")
    public String list(@PageableDefault(size = 10,sort = "addDate",direction = Sort.Direction.DESC) Pageable pageable, Model model){
        model.addAttribute("linkList",linkService.list(pageable));
        return "admin/link";
    }

    @PostMapping("/save")
    @ResponseBody
    public Map<String,Object> save(Link link,Model model){
        Map<String,Object>map = new HashMap<>();
        if(StringUtil.isEmpty(link.getLinkName()) || StringUtil.isEmpty(link.getLinkUrl())){
            map.put("success",false);
            map.put("errorInfo","各项输入都不能为空额！");
            return map;
        }
        link.setAddDate(new Date());
        linkService.save(link);
        if(link.getId() != null){
            map.put("success",true);
            map.put("message","修改成功！");
        }else{
            map.put("success",true);
            map.put("message","新增成功！");
        }

        return map;
    }


    @GetMapping("/edit")
    @ResponseBody
    @RequiresPermissions("编辑友链")
    public Map<String,Object> edit(@PathParam(value = "id") Long id){
        Map<String,Object>map = new HashMap<>();
        Link link = linkService.getById(id);
        if(link != null){
            map.put("success",true);
            map.put("link",link);
        }else{
            map.put("success",false);
            map.put("message","没有找到该信息");
        }
        return map;
    }

    @DeleteMapping("/delete")
    @ResponseBody
    @RequiresPermissions("删除友链")
    public Map<String,Object> delete(@PathParam(value = "id")Long id){
        Map<String,Object> map = new HashMap<>();
        if(id == null){
            map.put("success",false);
            map.put("message","操作异常！");
            return map;
        }
        linkService.delete(id);
        map.put("success",true);
        map.put("message","操作成功！");
        return map;
    }
}
