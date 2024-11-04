package com.CAUCSD.MUTCHIGI.user.security;

import com.CAUCSD.MUTCHIGI.user.UserRepository;
import com.CAUCSD.MUTCHIGI.user.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
    private final CustomUserDetailsService userDetailsService;
    private UserRepository userRepository;
    private UserService userService;


    public SecurityConfig(JwtUtil jwtUtil, CustomUserDetailsService userDetailsService, UserRepository userRepository, UserService userService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.
                authorizeHttpRequests( authorize -> authorize
                        .requestMatchers("/oauth2/authorization/google/**", "/oauth2/authorization/**").permitAll()
                        .requestMatchers( "/login/oauth2/code/google/**", "/login/oauth2/code/**").permitAll()
                        .requestMatchers( "/login/success").permitAll()
                        .requestMatchers( "/auth/google/**", "/oauth2/authorization/**", "/auth/callback/**", "/api/auth/google/**").permitAll()
                        .requestMatchers( "/token", "/authTest/google").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/error").permitAll()
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll() // 정적 리소스 허용
                        .requestMatchers("/room/**" ,"/room/idList", "/room/Entities" , "/quiz/images/**").permitAll() // 메인 화면 허용
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .anyRequest().authenticated()
                )
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                           .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // JWT를 사용하는 경우 상태 비저장
                ).oauth2Login(oauth2 -> oauth2 // OAuth2 로그인 설정
                        .authorizationEndpoint(authorization -> authorization
                                .baseUri("/oauth2/authorization") // 기본 URI 설정
                        )
                        .redirectionEndpoint(redirection -> redirection
                                .baseUri("/login/oauth2/code/*") // 리디렉션 URI 설정
                        )
                        .successHandler(new OAuth2LoginSuccessHandler(jwtUtil, userRepository, userService))
                )
                .addFilterBefore(new JwtRequestFilter(jwtUtil, userDetailsService), UsernamePasswordAuthenticationFilter.class); // JWT 필터 추가;


        return http.build();
    }

}
