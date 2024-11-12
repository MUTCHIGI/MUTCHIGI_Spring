package com.CAUCSD.MUTCHIGI.user.security;

import com.CAUCSD.MUTCHIGI.user.MemberRole;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/testing")
public class userLoginAPIController {

    private final JwtUtil jwtUtil;

    public userLoginAPIController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/login/success")
    public ResponseEntity<String> loginSuccess(@AuthenticationPrincipal OAuth2User principal) {
        String username = principal.getAttribute("email"); // 구글에서 이메일 정보 가져오기
        String token = jwtUtil.generateToken(username, MemberRole.Normal); // JWT 생성
        return ResponseEntity.ok(token); // JWT 반환
    }

    @GetMapping("/login/example")
    public ResponseEntity<String> getExample() {
        return ResponseEntity.ok("Hello World");
    }

    @GetMapping("/")
    public ResponseEntity<String> home() {
        return ResponseEntity.ok("홈페이지");
    }

    @GetMapping("/gettoken")
    public ResponseEntity<String> getToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String jwtToken = jwtUtil.generateToken(authentication.getName(), MemberRole.Normal);
        return ResponseEntity.ok(jwtToken); // JWT 반환
    }
}
