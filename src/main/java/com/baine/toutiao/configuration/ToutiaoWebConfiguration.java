package com.baine.toutiao.configuration;

import com.baine.toutiao.interceptor.LoginRequiredInterceptor;
import com.baine.toutiao.interceptor.PassportInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

@Component
public class ToutiaoWebConfiguration implements WebMvcConfigurer {
    @Autowired
    PassportInterceptor passportInterceptor;

    @Autowired
    LoginRequiredInterceptor loginRequiredInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(passportInterceptor);
        List<String> loginRequiredPaths = new ArrayList<>();
        loginRequiredPaths.add("/setting*");
        loginRequiredPaths.add("/msg/**/*");
        registry.addInterceptor(loginRequiredInterceptor).addPathPatterns(loginRequiredPaths);
    }
}
