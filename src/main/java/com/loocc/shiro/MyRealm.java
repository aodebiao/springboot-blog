package com.loocc.shiro;


import com.loocc.domain.User;
import com.loocc.service.UserService;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Set;

/**
 * 自定义Realm
 */
public class MyRealm extends AuthorizingRealm {
    @Autowired
    private UserService userService;

    /**
     * 当访问需要角色权限的资源时调用
     * 如果找到该角色，就不会第二次调用
     * 授权
     *
     * @param principalCollection
     * @return
     */
    //根据角色添加权限
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        String username = (String) principalCollection.getPrimaryPrincipal();
        User user = userService.findByUserName(username);
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        Set<String> roles = new HashSet<>();
        if ("admin".equals(user.getRole().getName())) {
            roles.add("admin");
            //博客
            info.addStringPermission("博客列表");
            info.addStringPermission("后台搜索");
            info.addStringPermission("进入新增博客编辑页面");
            info.addStringPermission("进入博客编辑页面");
            info.addStringPermission("更新或者新增博客");
            info.addStringPermission("删除博客");
            //分类
            info.addStringPermission("分类列表");
            info.addStringPermission("新增分类");
            info.addStringPermission("修改分类");
            info.addStringPermission("删除分类");
            //标签
            info.addStringPermission("标签列表");
            info.addStringPermission("新增标签");
            info.addStringPermission("修改标签");
            info.addStringPermission("删除标签");
            //用户
            info.addStringPermission("用户列表");
            info.addStringPermission("修改角色");
            info.addStringPermission("修改状态");
            info.addStringPermission("搜索用户");
            //友链管理
            info.addStringPermission("友链列表");
            info.addStringPermission("编辑友链");
            info.addStringPermission("删除友链");

            info.addStringPermission("管理员登录");
            info.addStringPermission("管理员登录页面");
        }
        info.setRoles(roles);
        return info;
    }

    /**
     * 认证
     * 控制器调用login方法时调用
     *
     * @param authenticationToken
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        System.out.println("测试--------------");
        //执行subject的login方法的时候自动调用此方法
        String userName = (String) authenticationToken.getPrincipal();
        User user = userService.findByUserName(userName);
        if (null == user) {
            return null;
        } else {
            //第二个参数的名字为数据库的密码
            AuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(user.getUsername(),
                    user.getPassword(), "");
            return authenticationInfo;
        }
    }
}
