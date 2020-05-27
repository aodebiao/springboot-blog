package com.loocc.shiro;

import at.pollux.thymeleaf.shiro.dialect.ShiroDialect;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * shiro 配置类
 *  要有 Subject: 主体
 *      SecurityManager: 安全管理器
 *      Realm: Shiro连接数据的桥梁
 */
@Configuration
public class ShiroConfig {
    /**
     * 创建ShiroFilterFactoryBean
     */
    @Bean
    public ShiroFilterFactoryBean ShiroFilterFactoryBean(SecurityManager securityManager){
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        //添加shiro内置过虑器
        /**
         * anon:无需认证(登录)就可以访问
         * authc:必须认证后才能访问
         * user:如果使用rememberMe功能可以访问
         * perms:该资源必须得到资源授权才能访问
         * role:该资源须得到角色权限才可以访问
         */
        Map<String,String> filterMap = new LinkedHashMap<>();
        filterMap.put("/css/**","anon");
        filterMap.put("/images/**","anon");
        filterMap.put("/js/**","anon");
        filterMap.put("/lib/**","anon");

        filterMap.put("/user/toLogin","anon");//登录页面
        filterMap.put("/user/toRegister","anon");//注册页面
        filterMap.put("/user/login","anon");//登录功能
        filterMap.put("/user/register","anon");//注册功能
        filterMap.put("/user/sendEmail","anon");
        filterMap.put("/user/goFindUser","anon");
        filterMap.put("/user/findUser","anon");
        filterMap.put("/user/modifyUser","anon");
        filterMap.put("/user/showMes","anon");
        filterMap.put("/user/bindUser","anon");
        filterMap.put("/archives","anon");

        filterMap.put("/QQ/qqlogin","anon");
        filterMap.put("/connect","anon");

        filterMap.put("/user/**","authc");
        filterMap.put("/comments","authc");

        filterMap.put("/admin/unauthorized","anon");
        filterMap.put("/admin/**","roles[admin]");

        filterMap.put("/admin/logout","authc");

        //filterMap.put("/admin/**","roles[admin]");

        shiroFilterFactoryBean.setLoginUrl("/user/toLogin");
        shiroFilterFactoryBean.setUnauthorizedUrl("/admin/unauthorized");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterMap);
        return shiroFilterFactoryBean;
    }

    /**
     * 创建DefaultWebSecurityManager
     */
    @Bean("securityManager")
    public SecurityManager getSecurityManager(MyRealm realm){
        DefaultWebSecurityManager securityManager =  new DefaultWebSecurityManager();
        securityManager.setRealm(realm);
        return  securityManager;
    }

    /**
     * 创建Realm
     */
    @Bean
    public MyRealm getRealm(RetryLimitHashedCredentialsMatcher retryLimitHashedCredentialsMatcher){
        MyRealm myRealm =new MyRealm();
        myRealm.setCredentialsMatcher(retryLimitHashedCredentialsMatcher);
        return myRealm;
    }

    @Bean("credentialsMatcher")
    public RetryLimitHashedCredentialsMatcher retryLimitHashedCredentialsMatcher(){
        RetryLimitHashedCredentialsMatcher retryLimitHashedCredentialsMatcher = new RetryLimitHashedCredentialsMatcher();
        ////如果密码加密,可以打开下面配置
        ////加密算法的名称
        //retryLimitHashedCredentialsMatcher.set("MD5");
        ////配置加密的次数
        //retryLimitHashedCredentialsMatcher.setHashIterations(10);
        ////是否存储为16进制
        //retryLimitHashedCredentialsMatcher.setStoredCredentialsHexEncoded(true);
        return retryLimitHashedCredentialsMatcher;
    }


    /**
     * 配置ShiroDialect,用于thymeleaf和shiro标签配置
     */
    @Bean
    public ShiroDialect getShiroDialect(){
        return new ShiroDialect();
    }

    //Shiro生命周期
    @Bean
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor(){
        return new LifecycleBeanPostProcessor();
    }

    /**
     * 开启shiro的注解，如@RequiresRoles,@RequiresPermissions,需要借助SpringAOP扫描
     * 使用shiro注解 的类，在必要时进行安全验证,配置以下两个bean即可实现这个功能
     */

    @Bean
    @DependsOn("lifecycleBeanPostProcessor")
    public DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator(){
        DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        advisorAutoProxyCreator.setProxyTargetClass(true);
        return advisorAutoProxyCreator;
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(@Autowired SecurityManager securityManager){
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }
}

