package com.example.hnh.global.config;

import com.example.hnh.global.config.interceptor.MemberRoleInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final MemberRoleInterceptor memberRoleInterceptor;

    public WebConfig(MemberRoleInterceptor memberRoleInterceptor) {
        this.memberRoleInterceptor = memberRoleInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 멤버 권한 인터셉터
        registry.addInterceptor(memberRoleInterceptor);
    }
}