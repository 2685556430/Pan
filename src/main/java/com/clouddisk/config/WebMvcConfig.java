package com.clouddisk.config;

import com.clouddisk.constants.Constant;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * TODO 全局拦截器配置
 *
 * @author ddwl.
 * @date 2022/5/27 10:40
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    // 解决MainInterceptor 中 FileService 无法自动装配的问题 不然会出现空指针异常 具体原因还需要进一步了解！！！
    @Bean
    public MainInterceptor setBean1(){
        return new MainInterceptor();
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //注册UserLoginInterceptor拦截器
        InterceptorRegistration registration = registry.addInterceptor(setBean1());
        registration.addPathPatterns("/**"); //所有路径都被拦截
        registration.excludePathPatterns(Constant.excludePath); // 设定放行路径

    }
}
