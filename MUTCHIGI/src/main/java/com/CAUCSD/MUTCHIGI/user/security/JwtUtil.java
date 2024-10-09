package com.CAUCSD.MUTCHIGI.user.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import java.security.Key;
import java.util.Date;

import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {
    private final Key SECRET_KEY = Keys.hmacShaKeyFor("sadfsadfasgadfsgfdsgsdgsdfsdafdsfgdasfgdsffgfsdgsfdgdsgsdfgdsgsdfgsdfgsdg".getBytes());

    // JWT 생성
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 6시간
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
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // 토큰 만료 여부 확인
    private boolean isTokenExpired(String token) {

        return extractAllClaims(token)
                .getExpiration().before(new Date());
    }
}
