package com.CAUCSD.MUTCHIGI.user.security;

import com.CAUCSD.MUTCHIGI.user.MemberRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    @Value("${example.testkey}")
    private String jwtKey;

    private Key SECRET_KEY;

    @PostConstruct
    public void init() {
        // jwtKey가 주입된 후에 SECRET_KEY 초기화
        SECRET_KEY = Keys.hmacShaKeyFor(jwtKey.getBytes());
        System.out.println("SECRET_KEY IS : " + jwtKey);
    }

    // JWT 생성
    public String generateToken(String username, MemberRole memberRole) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", memberRole.name()); // 단일 역할을 리스트로 감싸기

        System.out.println("토큰 생성중");
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10시간
                .signWith(SECRET_KEY)
                .compact();
    }

    // JWT 검증
    public boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }

    // JWT에서 사용자 이름 추출
    public String extractUsername(String token) {

        return extractAllClaims(token)
                .getSubject();
    }

    // JWT의 모든 클레임 추출
    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // 토큰 만료 여부 확인
    public boolean isTokenExpired(String token) {

        return extractAllClaims(token)
                .getExpiration().before(new Date());
    }

    public List<GrantedAuthority> getAuthoritiesFromClaims(Claims claims) {
        System.out.println("내부 claim" +claims);
        String role = claims.get("roles", String.class); // "roles" 클레임에서 단일 역할 추출
        return Collections.singletonList(new SimpleGrantedAuthority(role)); // 단일 역할을 GrantedAuthority로 변환하여 리스트로 감싸기
    }

}
