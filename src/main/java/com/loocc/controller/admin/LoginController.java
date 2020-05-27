package com.loocc.controller.admin;


import com.loocc.domain.User;
import com.loocc.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class LoginController {


    @Autowired
    private UserService userService;

    @GetMapping("/")
    @RequiresPermissions("管理员登录页面")
    public String loginPage() {
        return "admin/login";
    }

    @GetMapping("/index")
    public String index(){
        return "admin/index";
    }


    @ResponseBody
    @PostMapping("/login")
    @RequiresPermissions("管理员登录")
    public Map<String,Object> login(@RequestParam String username,
                                    @RequestParam String password) {
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(username, password);
        Map<String,Object>map = new HashMap<>();
        try {
            subject.login(token);
            User user = userService.findByUserName(username);
            user.setPassword(null);
            subject.getSession().setAttribute("currentUser",user);
            map.put("success",true);
            return map;
        } catch (UnknownAccountException e) {
            map.put("success",false);
           map.put("message","账号不存在!");
           return map;
        }catch (IncorrectCredentialsException e){
            map.put("success",false);
            map.put("message","密码错误!");
            return map;
        }
        //if (user != null) {
        //    user.setPassword(null);
        //    session.setAttribute("user",user);
        //    return "admin/index";
        //} else {
        //    attributes.addFlashAttribute("message", "用户名和密码错误!");
        //    return "redirect:/admin";
        //}
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        SecurityUtils.getSubject().logout();
        return "redirect:/";
    }

    @GetMapping("/unauthorized")
    public String unauthorized(){
        return "/error/401";
    }
}
