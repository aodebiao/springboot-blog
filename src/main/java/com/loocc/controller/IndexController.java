package com.loocc.controller;


import com.loocc.domain.Role;
import com.loocc.domain.User;
import com.loocc.essevice.BlogEsService;
import com.loocc.service.*;
import com.loocc.util.Const;
import com.loocc.util.StringUtil;
import com.loocc.util.VerifyCode;
import com.qq.connect.QQConnectException;
import com.qq.connect.api.OpenID;
import com.qq.connect.api.qzone.UserInfo;
import com.qq.connect.javabeans.qzone.UserInfoBean;
import com.qq.connect.oauth.Oauth;
import org.apache.shiro.SecurityUtils;
import com.qq.connect.javabeans.AccessToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

@Controller
public class IndexController {
    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;

    @Autowired
    private BlogService blogService;

    @Autowired
    private BlogTypeService typeService;

    @Autowired
    private TagService tagService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private BlogEsService blogEsService;

    @Autowired
    private LinkService linkService;

    @GetMapping("/")
    public String index(@PageableDefault(size = 8, sort = {"updateTime"}, direction = Sort.Direction.DESC) Pageable pageable,
                        Model model) {
        model.addAttribute("page",blogService.findPublishedBlog(pageable));
        model.addAttribute("types", typeService.listTypeTop(6));
        model.addAttribute("tags", tagService.listTagTop(10));
        model.addAttribute("recommendBlogs", blogService.listRecommendBlogTop(8));
        model.addAttribute("links",linkService.list(PageRequest.of(0,10,Sort.Direction.DESC,"addDate")));
        return "index";
    }


    @Transactional
    @PostMapping("/search")
    public String search(@PageableDefault(size = 8, sort = {"createTime"}, direction = Sort.Direction.DESC) Pageable pageable,
                         @RequestParam String query, Model model) {
        String[] searchField = {"title","content","description"};
        //搜索结果按创建时间降序排序
        model.addAttribute("page",blogEsService.highLightQuery( "DESC","createTime",searchField,query.toLowerCase(),pageable));
        //model.addAttribute("page", blogService.listBlog("%"+query+"%", pageable));
        model.addAttribute(" query", query);
        return "search";
    }

    @GetMapping("/blog/{id}")
    public String blog(@PathVariable Long id, Model model) {
        model.addAttribute("blog", blogService.getAndConvert(id));
        blogEsService.updateEsBlog(id);
        model.addAttribute("comments",commentService.listCommentByBlogId(id));
        return "blog";
    }

    @GetMapping("/footer/newblog")
    public String newblogs(Model model) {
        model.addAttribute("newblogs", blogService.listRecommendBlogTop(3));
        return "_fragments :: newblogList";
    }

    @RequestMapping("/getVerifyCode")
    public void getVerificationCode(HttpServletResponse response) {
        OutputStream os = null;
        try {
            //功能是生成验证码字符并加上噪点，干扰线，返回值为验证码字符
            BufferedImage verifyImg = new BufferedImage(Const.IMAGE_WIDTH, Const.IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
            String randomText = VerifyCode.drawRandomText(Const.IMAGE_WIDTH, Const.IMAGE_HEIGHT, verifyImg);
            //存到session中，后面比对
            SecurityUtils.getSubject().getSession().setAttribute("verifyCode", randomText);
            response.addCookie(new Cookie("vcode",randomText));
            response.setContentType("image/png");//必须设置响应内容类型为图片，否则前台不识别
            os = response.getOutputStream(); //获取文件输出流
            ImageIO.write(verifyImg, "png", os);//输出图片流
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                os.close();//关闭流
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * qq登录回调
     */
    @RequestMapping("/connect")
    public String qqCallbck(HttpServletRequest request, HttpServletResponse response, HttpSession session){
        response.setContentType("text/html;charset=utf-8");
        try {
            AccessToken accessToken = new Oauth().getAccessTokenByRequest(request);
            String accessTokenStr = null;
            String openId = null;
            String state = request.getParameter("state");
            String session_state = (String) session.getAttribute("qq_connect_state");
            if (StringUtil.isEmpty(session_state) || !session_state.equals(state)) {
                System.out.println("非法请求");
                return "redirect:/";
            }
            accessTokenStr = accessToken.getAccessToken();
            if (StringUtil.isEmpty(accessTokenStr)) {
                System.out.println("没有获取到响应参数！");
                return "redirect:/";
            }
            session.setAttribute("accessToken", accessTokenStr);
            OpenID openIdObj = new OpenID(accessTokenStr);
            openId = openIdObj.getUserOpenID();
            UserInfo qzoneUserInfo = new UserInfo(accessTokenStr, openId);
            UserInfoBean userInfoBean = qzoneUserInfo.getUserInfo();
            if (userInfoBean == null || userInfoBean.getRet() != 0 || StringUtil.isNotEmpty(userInfoBean.getMsg())) {
                System.out.println("没有对应的qq信息！");
                return "redirect:/";
            }
            //登录后再绑定
            User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
            if (currentUser != null && currentUser.getId() != null && StringUtil.isNotEmpty(currentUser.getUsername()) && StringUtil.isNotEmpty(currentUser.getEmail()) && StringUtil.isEmpty(currentUser.getOpenId())) {
                currentUser.setOpenId(openId);
                userService.save(currentUser);
                session.setAttribute(Const.CURRENT_USER, currentUser);
                return "redirect:/";
            }


            //获取成功
            User user = userService.findByOpenId(openId);
            if (user == null) {//该用户第一次登录，系统帮他注册
                user = new User();
                user.setOpenId(openId);
                user.setNickname(userInfoBean.getNickname());
                user.setAvatar(userInfoBean.getAvatar().getAvatarURL30());
                user.setStatus(true);
                user.setBlogs(null);
                user.setCreateTime(new Date());
                user.setUpdateTime(new Date());

                int sex = 0;
                if (userInfoBean.getGender().trim().equals("男") || userInfoBean.getGender() == null) {
                    sex = 1;
                }
                user.setGender(sex);
                session.setAttribute("currentUser", user);
            } else {//注册过，更新用户信息，存入session
                user.setOpenId(openId);
                user.setNickname(userInfoBean.getNickname());
                user.setAvatar(userInfoBean.getAvatar().getAvatarURL30());
                user.setUpdateTime(new Date());
                Subject subject = SecurityUtils.getSubject();
                UsernamePasswordToken token = new UsernamePasswordToken(user.getUsername(), user.getPassword());
                subject.login(token);
                user.setPassword(null);
                session.setAttribute(Const.CURRENT_USER, user);

            }
        } catch (QQConnectException e) {
            e.printStackTrace();
        }

        return "redirect:/";
    }
}
