package com.loocc.controller.admin;


import com.loocc.domain.Role;
import com.loocc.domain.User;
import com.loocc.service.RoleService;
import com.loocc.service.UserService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@Controller
@RequestMapping(value = "/admin")
public class UserManagerController {
    @Autowired
    UserService userService;

    @Autowired
    RoleService roleService;

    @GetMapping(value = "/listUser")
    @RequiresPermissions("用户列表")
    public String listUser(@PageableDefault(size = 8, sort = "createTime", direction = Sort.Direction.ASC) Pageable pageable, Model model) {
        model.addAttribute("page", userService.findAllByRoleNot(pageable));
        model.addAttribute("roles", roleService.findAll());
        return "admin/users";
    }

    @RequestMapping(value = "/modifyRole")
    @RequiresPermissions("修改角色")
    public String modifyRole(@RequestParam("userId") Long id, @PageableDefault(size = 8, sort = "createTime",
            direction = Sort.Direction.ASC) Pageable pageable,String roleName,Model model) {
        if(roleName.equals("")){
                //未选择角色则不修改
        }else{
            List<Role> roles = roleService.findAll();
            boolean flag = false;
            for (Role role: roles) {
                flag = role.getName().equals(roleName);
                if(flag){
                    break;
                }
            }
            if (roles.isEmpty() || !flag) {
                userService.updateRole(roleService.findByRoleName("user"), id);
                model.addAttribute("message","发生异常,该用户角色将被重置为普通用户！");
            }
            userService.updateRole(roleService.findByRoleName(roleName), id);
        }
        model.addAttribute("page", userService.findAllByRoleNot(pageable));
        model.addAttribute("roles", roleService.findAll());
        model.addAttribute("message","修改角色成功！");
        return "admin/users :: userList";
    }


    @RequestMapping(value = "/modifyStatus")
    @RequiresPermissions("修改状态")
    public String modifyStatus(@RequestParam(value = "userId") Long id,Model model,@PageableDefault(size = 8, sort = "createTime", direction = Sort.Direction.ASC) Pageable pageable){
        User user = userService.findById(id);
        if(user.isStatus()){
            userService.updateStatus(false,id);
        }else{
            userService.updateStatus(true,id);
        }
        model.addAttribute("page", userService.findAllByRoleNot(pageable));
        model.addAttribute("roles", roleService.findAll());
        model.addAttribute("message","操作成功！");
        return "admin/users :: userList";
    }

    @RequestMapping(value = "/searchUser")
    @RequiresPermissions("搜索用户")
    public String searchUser(@RequestParam(value = "username")String username,Model model,
                             @PageableDefault(size = 1) Pageable pageable){
        if(username.trim().equals("")){
            return "redirect:/admin/users";
        }
        model.addAttribute("page",userService.searchUser(username,pageable));
        model.addAttribute("roles", roleService.findAll());
        return "admin/users::userList";
    }

}
