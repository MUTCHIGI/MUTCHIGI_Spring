package com.CAUCSD.MUTCHIGI.user;

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
@RequestMapping("/auth")
public class UserController {

    private static final String CLIENT_ID = "111904407916-7npkvko09qv63g09jqtcdnbbikp3ki7b.apps.googleusercontent.com";

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/google")
    @Operation(summary = "Google 토큰으로 계정 정보 얻기", description = "구글 로그인을 통해 얻은 토큰(JWT)를 따옴표 없이 입력하여 계정 정보를 얻는 API입니다.")
    public ResponseEntity<Map<String, Object>> loginGoogle(@RequestBody String idGoogleToken){
        try{

            System.out.println(idGoogleToken);
            GsonFactory jsonFactory = GsonFactory.getDefaultInstance();
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), jsonFactory)
                    .setAudience(Collections.singletonList(CLIENT_ID))
                    .build();

            GoogleIdToken idToken = verifier.verify(idGoogleToken);
            if(idToken != null){
                GoogleIdToken.Payload payload = idToken.getPayload();

                UserDTO userDTO = new UserDTO();
                userDTO.setEmail(payload.getEmail());
                userDTO.setPlatformUserId((String) payload.get("sub"));
                userDTO.setName((String) payload.get("name"));
                userDTO.setProfileImageURL((String) payload.get("picture"));
                userDTO.setProviderId(1);

                UserEntity registeredUser = userService.registerUser(userDTO);

                String jwt = jwtUtil.generateToken(registeredUser.getEmail());

                Map<String, Object> responseBody = new HashMap<>();

                responseBody.put("token", jwt);
                responseBody.put("userId", registeredUser.getUserId());
                responseBody.put("email", registeredUser.getEmail());
                responseBody.put("name", registeredUser.getName());
                responseBody.put("ProfileImageURL", registeredUser.getProfileImageURL());
                responseBody.put("providerName", registeredUser.getProvider().getProviderName());

                return ResponseEntity.ok(responseBody);
            }else{
                return ResponseEntity.status(400).body(Map.of("message", "Invalid ID Token"));
            }
        }
        catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Server error"));
        }
    }

}
