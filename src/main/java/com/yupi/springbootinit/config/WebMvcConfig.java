package com.yupi.springbootinit.config;

import com.yupi.springbootinit.handler.AdminLoginInterceptor;
import com.yupi.springbootinit.handler.UserLoginInterceptor;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author guoyixing
 * 2022/11/19
 */
@Configuration
@AllArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private UserLoginInterceptor userLoginInterceptor;
    private AdminLoginInterceptor adminLoginInterceptor;

    @Autowired
    public void setUserLoginInterceptor(UserLoginInterceptor userLoginInterceptor) {
        this.userLoginInterceptor = userLoginInterceptor;
    }

    @Autowired
    public void setAdminLoginInterceptor(AdminLoginInterceptor adminLoginInterceptor) {
        this.adminLoginInterceptor = adminLoginInterceptor;
    }


    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowCredentials(true)
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("*")
                .exposedHeaders("*");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //TODO 给其他接口添加拦截器，同时增加认证拦截逻辑

        //添加用户拦截器
        registry.addInterceptor(userLoginInterceptor)
                .addPathPatterns("/user/*")           //拦截用户所有
                .excludePathPatterns("/user/login")        //放行登录接口
                .excludePathPatterns("/user/identify")     //放行认证接口
                .excludePathPatterns("/user/info/{id}")    //放行获取用户信息接口
                .addPathPatterns("/post/*")           //拦截帖子所有
                .excludePathPatterns("/post/list/page")   //放行帖子列表接口
                .addPathPatterns("/task_user/*")     ;      //拦截任务所有

        //添加管理员拦截器，拦截所有接口
        registry.addInterceptor(adminLoginInterceptor)
                .addPathPatterns("/admin/*")
                .excludePathPatterns("/admin/login")
                .addPathPatterns("/task_admin/*")
                .addPathPatterns("/student/*");
    }
}
