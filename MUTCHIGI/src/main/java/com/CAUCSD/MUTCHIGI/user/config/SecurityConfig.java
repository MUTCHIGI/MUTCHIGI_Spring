package com.CAUCSD.MUTCHIGI.user.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig{

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public SecurityConfig(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.
                authorizeHttpRequests( authorize -> authorize
                        .requestMatchers("/oauth2/authorization/google/**").permitAll()
                        .requestMatchers( "/login/oauth2/code/google/**").permitAll()
                        .requestMatchers( "/login/success").permitAll()
                        .requestMatchers("/swagger-ui/index.html/**", "/v3/api-docs/**", "/swagger-ui/**", "/error").permitAll()
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll() // 정적 리소스 허용
                        .anyRequest().authenticated()
                )
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) // JWT를 사용하는 경우 상태 비저장
                ).oauth2Login(oauth2 -> oauth2 // OAuth2 로그인 설정
                        .authorizationEndpoint(authorization -> authorization
                                .baseUri("/oauth2/authorization") // 기본 URI 설정
                        )
                        .redirectionEndpoint(redirection -> redirection
                                .baseUri("/login/oauth2/code/*") // 리디렉션 URI 설정
                        )
                )
                .addFilterBefore(new JwtRequestFilter(jwtUtil, userDetailsService), UsernamePasswordAuthenticationFilter.class); // JWT 필터 추가;


        return http.build();
    }

}
