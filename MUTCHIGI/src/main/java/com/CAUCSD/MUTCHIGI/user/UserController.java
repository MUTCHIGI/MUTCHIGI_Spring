package com.CAUCSD.MUTCHIGI.user;

import com.CAUCSD.MUTCHIGI.user.security.CustomUserDetailsService;
import com.CAUCSD.MUTCHIGI.user.security.JwtUtil;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/authTest")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/google")
    @Operation(summary = "Google 토큰으로 계정 정보 얻기", description = "구글 로그인을 통해 얻은 토큰(JWT)를 따옴표 없이 입력하여 계정 정보를 얻는 API입니다.")
    public ResponseEntity<UserEntity> loginGoogle(@RequestBody String springToken){
        try{
            String username = jwtUtil.extractUsername(springToken);
            UserEntity userEntity = userRepository.findByPlatformUserId(username);

            if(springToken != null){

                return ResponseEntity.ok(userEntity);
            }
        }
        catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.badRequest().body(new UserEntity());
        }
        return ResponseEntity.internalServerError().body(new UserEntity());
    }

}
