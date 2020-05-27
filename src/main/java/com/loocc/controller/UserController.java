package com.loocc.controller;

import com.loocc.domain.User;
import com.loocc.service.UserService;
import com.loocc.util.Const;
import com.loocc.util.MyBeanUtils;
import com.loocc.util.StringUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpRequest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.mail.Message;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;


    @Resource
    private MimeMessage mimeMessage;

    @RequestMapping("/toRegister")
    public String toRegister() {//跳转到登录界面
        return "register";
    }

    @RequestMapping("/toLogin")
    public String toLogin() {//跳转到登录界面
        return "login";
    }

    @PostMapping("/register")
    @ResponseBody
    public Map<String, Object> register(@Valid User user, BindingResult result) {
        Map<String, Object> map = new HashMap<>();
        if (!user.getVerificationCode().equals((String) SecurityUtils.getSubject().getSession().getAttribute("vcode"))) {
            map.put("success", false);
            map.put("message", "注册失败，验证码输入有误！");
            return map;
        }
        if (result.hasErrors()) {
            map.put("success", false);
            map.put("message", result.getFieldError().getDefaultMessage());
            return map;
        } else if (null != userService.findByUserName(user.getUsername())) {
            map.put("success", false);
            map.put("message", "可恶啊,名字被抢占了！");
            return map;
        } else if (null != userService.findByEmail(user.getEmail())) {
            map.put("success", false);
            map.put("message", "可恶啊,邮箱被抢占了！");
            return map;
        } else if (!user.getPassword().equals(user.getCheck_password())) {

            //userService.save(user);
            map.put("message", "注册失败，两次密码不一致~");
            map.put("success", false);
            return map;
        } else {
            userService.save(user);
            map.put("ok", "注册成功，可以登录了额~");
            map.put("success", true);
            return map;
        }
    }

    /**
     * 登录
     *
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/login")
    public Map<String, Object> login(User user, @RequestParam(value = "verifyCode") String verifyCode) {
        Subject subject = SecurityUtils.getSubject();
        String vcode = (String) SecurityUtils.getSubject().getSession().getAttribute("verifyCode");
        Map<String, Object> map = new HashMap<>();
        if (!verifyCode.toLowerCase().equals(vcode.toLowerCase())) {
            map.put("message", "验证码输入有误！");
            return map;
        }
        if (StringUtil.isEmpty(user.getUsername())) {
            map.put("success", false);
            map.put("message", "用户名不能为空！");
            return map;
        } else if (StringUtil.isEmpty(user.getPassword())) {
            map.put("success", false);
            map.put("message", "密码不能为空！");
            return map;
        }
        //封装用户数据
        UsernamePasswordToken token = new UsernamePasswordToken(user.getUsername(), user.getPassword());
        try {
            subject.login(token);//没有异常则登录成功，反之失败
            User currentUser = userService.findByUserName(user.getUsername());
            currentUser.setPassword(null);
            currentUser.setBlogs(null);
            subject.getSession().setAttribute("currentUser", currentUser);
            map.put("success", true);
        } catch (UnknownAccountException e) {
            map.put("message", "用户名不存在！");
            return map;
        } catch (IncorrectCredentialsException e) {
            map.put("message", "密码错误！");
            return map;
        }

        return map;
    }


    @GetMapping(value = "/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("currentUser");
        return "redirect:/";
    }

    @ResponseBody
    @PostMapping("/sendEmail")
    public Map<String, Object> sendEmail(String email, HttpSession session, boolean flag) {
        Map<String, Object> map = new HashMap<>();
        if (flag) {
            if (StringUtil.isEmpty(email)) {
                map.put("success", false);
                map.put("message", "邮箱地址不能为空！");
                return map;
            }
            if (null != userService.findByEmail(email)) {
                map.put("success", false);
                map.put("message", "邮箱已被占用了,换一个试试?");
                return map;
            }
        }else{
            if (null == userService.findByEmail(email)) {
                map.put("success", false);
                map.put("message", "邮箱未被注册,请检查输入邮箱。");
                return map;
            }
        }
        String VCode = StringUtil.getSixVCode();
        try {
            mimeMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            mimeMessage.setSubject(Const.EMAIL_MESSAGE);//标题
            mimeMessage.setText(VCode);
            Transport.send(mimeMessage);
            System.out.println(VCode);
            session.setAttribute("vcode", VCode);
            map.put("success", true);
            map.put("time_interval", 60);
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            map.put("success", false);
            //map.put("errorInfo", "邮件发送失败,请检查邮箱地址！");
            map.put("message", e.toString());
            return map;
        }


    }


    @GetMapping("/goModify")
    public String goModify(Model model) {
            User user = (User) SecurityUtils.getSubject().getSession().getAttribute("currentUser");
        if(user.getAvatar().equals(Const.BOY_AVATAR) || user.getAvatar().equals(Const.GIRL_AVATAR)){
            user.setAvatar(null);
        }
        model.addAttribute("message","不修改密码，则不用填！");
        model.addAttribute("user", user);
        model.addAttribute("title","修改信息");
        return "modify-user";
    }

    @ResponseBody
    @PostMapping("/modifyUser")
    public Map modifyUser(User user,HttpServletRequest request) {
        Map map = new HashMap();
        if (!checkVerification(user, "vcode")) {
            map.put("success", false);
            map.put("message", "修改失败，验证码输入有误！");
            return map;
        }
        //把前台传过来的不可修改的值设为null
        user.setUsername(null);
        user.setEmail(null);
        user.setGender(null);
        User currentUser = (User) SecurityUtils.getSubject().getSession().getAttribute("currentUser");
        if (currentUser == null) {//忘记密码
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals("email")) {
                    return findPassword(cookie.getValue(), user);
                }
            }
            map.put("success",false);
            map.put("message","请不要禁用浏览器的cookie！");
            return map;
        } else {//修改资料
            if (!user.getPassword().trim().equals("") && !user.getCheck_password().trim().equals("")) {//修改密码
                return modifyPassword(user,SecurityUtils.getSubject().getSession());
            } else {
                return modifyMes(user, SecurityUtils.getSubject().getSession());//不修改密码
            }

        }
    }

    public boolean checkVerification(User user,String vcode){
        String code = (String)SecurityUtils.getSubject().getSession().getAttribute(vcode);
        return code.equals(user.getVerificationCode());
    }

    public boolean check_password(User user){
        return user.getPassword().equals(user.getCheck_password());
    }



    public Map findPassword(String email,User user){
        Map map = new HashMap();
        User userDB = userService.findByEmail(email);
        if(userDB == null){
            map.put("success",false);
            map.put("message","没有找到该邮箱对应的用户！");
            map.put("title","找回密码！");
            map.put("flag",3);//跳到find-user界面
        }else{
            if(check_password(user)){
                user.setStatus(userDB.isStatus());
                user.setCheck_password(null);
                BeanUtils.copyProperties(user,userDB,MyBeanUtils.getNullPropertyNames(user));
                userService.save(userDB);
                map.put("success",true);
                map.put("flag",1);//跳到登录界面
                map.put("title","找回密码");
                map.put("message","修改成功！");
            }else{
                map.put("success",false);
                map.put("message","修改失败，两次输入的密码不一致！");
            }
        }
        return map;
    }

    public Map modifyPassword(User user,Session session){
        Map map = new HashMap();
        User currentUser = (User)session.getAttribute("currentUser");
        if (check_password(user)) {
            user.setCheck_password(null);//数据库中没有此字段
            user.setStatus(currentUser.isStatus());
            BeanUtils.copyProperties(user, currentUser, MyBeanUtils.getNullPropertyNames(user));
            userService.updateUser(currentUser);
            map.put("success", true);
            map.put("title", "修改信息");
            map.put("message", "修改成功！");
            map.put("flag",3);//跳到主页
            return map;
        } else {
            map.put("success", false);
            map.put("message", "两次输入的密码不一致！");
            return map;
        }
    }

    public Map modifyMes(User user,Session session){//不修改密码时，调用
        Map map = new HashMap();
        User currentUser = (User)session.getAttribute("currentUser");
        user.setPassword(null);
        BeanUtils.copyProperties(user, currentUser, MyBeanUtils.getNullPropertyNames(user));
        int a = 4;
        userService.updateUser(currentUser);
        SecurityUtils.getSubject().getSession().setAttribute("currentUser", currentUser);
        map.put("success", true);
        map.put("message", "修改成功！");
        return map;
    }





    @GetMapping("/findUser")
    @ResponseBody
    public Map findUser(String email, String verificationCode, HttpServletResponse response) {
            Map map = new HashMap();
        String vcode = (String) SecurityUtils.getSubject().getSession().getAttribute("vcode");
        if (!verificationCode.equals(vcode)) {
            map.put("success", false);
            map.put("message", "验证码输入有误！");
            return map;
        }
        User user = userService.findByEmail(email);
        if(user == null){
            map.put("success", false);
            map.put("message", "未查询到对应的用户！");
            return map;
        }else{
            response.addCookie(new Cookie("email",email));
            map.put("success", true);
            map.put("message", "查询到该用户！");
            return map;
        }

    }


    @GetMapping("/goFindUser")
    public String goFindUser() {
        return "find-user";
    }


    @GetMapping("/showMes")//跳转到查询到的用户的详情页面
    public String showMes(Model model, HttpServletRequest request){

        Cookie[] cookies = request.getCookies();
        for(Cookie cookie : cookies){
            if(cookie.getName().equals("email")){
                String email = cookie.getValue();
                User user = userService.findByEmail(email);
                user.setPassword(null);
                if(user.getAvatar().equals(Const.BOY_AVATAR) || user.getAvatar().equals(Const.GIRL_AVATAR)){
                            user.setAvatar(null);
                }
                model.addAttribute("message","请输入新的密码，牢记额~");
                model.addAttribute("user",user);
                model.addAttribute("title","找回密码");
                return "modify-user";
            }else{
                return "find-user";
            }
        }
        return "find-user";
    }

    /**
     * 绑定用户
     * @return
     */
    @PostMapping("/bindUser")
    @ResponseBody
    public Map<String,Object>bindUser(User user,HttpSession session){
        Map<String,Object> map = new HashMap<>();
        if(userService.findByUserName(user.getUsername()) != null){
            map.put("success", false);
            map.put("message", "用户名被占用！");
            return map;
        }
        if(!checkVerification(user,"vcode")){
            map.put("success", false);
            map.put("message", "验证码有误！");
            return map;
        }
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);

        BeanUtils.copyProperties(user,currentUser,MyBeanUtils.getNullPropertyNames(user));
        User qqUser = userService.save(currentUser);
        UsernamePasswordToken token = new UsernamePasswordToken(qqUser.getUsername(),Const.DEFAULT_PASSWORD);
        SecurityUtils.getSubject().login(token);
        qqUser.setPassword(null);
        session.setAttribute(Const.CURRENT_USER,qqUser);
        map.put("success",true);
        map.put("message","绑定成功！");
        map.put("password","初始密码为QWER111");
        return map;
    }

    @GetMapping("/unBind")
    @ResponseBody
    public Map<String,Object> unBind(HttpSession session){
        Map<String,Object>map = new HashMap<>();
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        userService.unBind(currentUser.getId());
        currentUser.setOpenId(null);
        session.setAttribute(Const.CURRENT_USER,currentUser);
        map.put("success",true);
        map.put("message","你已成功解除绑定！");
        return map;

    }

}
