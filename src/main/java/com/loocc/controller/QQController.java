package com.loocc.controller;

import com.loocc.service.UserService;
import com.qq.connect.QQConnectException;
import com.qq.connect.oauth.Oauth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * qq登录请求处理
 */
@Controller
@RequestMapping("/QQ")
public class QQController {
    @Autowired
    private UserService userService;

    /**
     * qq登录页面跳转
     */
    @RequestMapping("/qqlogin")
    public void qqLogin(HttpServletResponse response, HttpServletRequest request){
        response.setContentType("text/html;charset=utf-8");
        try {
            response.sendRedirect(new Oauth().getAuthorizeURL(request));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (QQConnectException e) {
            new QQConnectException("跳转失败！");
        }
    }
}
