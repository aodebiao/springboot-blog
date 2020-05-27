package com.loocc.interceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 过虑的配置类
 */
@Configuration
public class WebConfig  implements WebMvcConfigurer {
    //@Override
    //public void addInterceptors(InterceptorRegistry registry) {
    //    registry.addInterceptor(new LoginInterceptor())
    //            .addPathPatterns("/admin/**")       //拦截admin/下面的所有请求
    //            .excludePathPatterns("/admin")      //排除
    //            .excludePathPatterns("/admin/login")
    //            .excludePathPatterns(Arrays.asList("/css/**", "/js/**","/images/**","/lib/**"));
    //}
}
