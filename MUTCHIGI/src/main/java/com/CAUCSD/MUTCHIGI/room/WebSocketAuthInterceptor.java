package com.CAUCSD.MUTCHIGI.room;

import com.CAUCSD.MUTCHIGI.user.security.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

@Configuration
public class WebSocketAuthInterceptor  implements ChannelInterceptor {

    private final JwtUtil jwtUtil;

    @Autowired
    public WebSocketAuthInterceptor (JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;

    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);

        //System.out.println("여기는 진입하는가?"+headerAccessor);
        if (StompCommand.CONNECT.equals(headerAccessor.getCommand())) {
            String token = headerAccessor.getFirstNativeHeader("Authorization");

            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7); // "Bearer " 제거
                Authentication auth = authenticateUser(token);
                if (auth != null) {
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    headerAccessor.setUser(auth); // 인증된 사용자 설정

                } else {
                    throw new RuntimeException("Unauthorized");
                }
            }

        }
        System.out.println(message);
        return message;
    }


    private Authentication authenticateUser(String token) {
        // JWT 토큰 검증 로직
        if (jwtUtil.validateToken(token, jwtUtil.extractUsername(token))) { // 토큰과 사용자 이름 검증
            String username = jwtUtil.extractUsername(token); // JWT에서 사용자 이름 추출
            Claims claims = jwtUtil.extractAllClaims(token); // JWT에서 모든 클레임 추출
            System.out.println("여기 통과하는 중"+claims.getIssuer()+claims.getAudience());
            // 권한 추출 (예: roles 클레임에서 권한 가져오기)
            List<GrantedAuthority> authorities = jwtUtil.getAuthoritiesFromClaims(claims); // 클레임에서 권한 추출
            System.out.println("인증 확인" + authorities);
            // UsernamePasswordAuthenticationToken 생성
            return new UsernamePasswordAuthenticationToken(username, token, authorities);
        }
        return null; // 토큰이 유효하지 않은 경우 null 반환
    }
}
