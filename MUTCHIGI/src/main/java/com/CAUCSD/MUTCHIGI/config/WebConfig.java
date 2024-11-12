package com.CAUCSD.MUTCHIGI.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 모든 경로에 임시 허용 -> 추후 컨트롤러 단위로 CORS 허용 방식으로 수정 예정
                .allowedOriginPatterns("*")//허용할
                .allowedMethods("GET","POST","PUT","DELETE","OPTIONS")
                .allowedHeaders("*") // 허용할 헤더들
                .allowCredentials(true); // 자격증명(쿠키, HTTP 인증정보) 허용
    }
}
