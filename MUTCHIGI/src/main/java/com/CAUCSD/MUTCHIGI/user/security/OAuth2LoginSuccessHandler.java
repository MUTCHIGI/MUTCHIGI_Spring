package com.CAUCSD.MUTCHIGI.user.security;

import com.CAUCSD.MUTCHIGI.user.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.hibernate.cfg.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Configuration
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;

    private RequestCache requestCache = new HttpSessionRequestCache();


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    public OAuth2LoginSuccessHandler(JwtUtil jwtUtil, UserRepository userRepository, UserService userService) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        System.out.println("onAuthenticationSuccess" + authentication.getPrincipal());

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        UserDTO newUserDTO = new UserDTO();

        if(userRepository.findByPlatformUserId(authentication.getName()) == null) {
            newUserDTO.setEmail(oAuth2User.getAttribute("email"));
            newUserDTO.setPlatformUserId(oAuth2User.getAttribute("sub"));
            newUserDTO.setName(oAuth2User.getAttribute("name"));
            newUserDTO.setProfileImageURL(oAuth2User.getAttribute("picture"));
            newUserDTO.setProviderId(1);
            UserEntity newRegisterUser = userService.registerUser(newUserDTO);
        }else {
            UserEntity extinguishUser = userRepository.findByPlatformUserId(authentication.getName());
            newUserDTO.setEmail(extinguishUser.getEmail());
            newUserDTO.setPlatformUserId(extinguishUser.getPlatformUserId());
            newUserDTO.setName(extinguishUser.getName());
            newUserDTO.setProfileImageURL(extinguishUser.getProfileImageURL());
            newUserDTO.setProviderId(extinguishUser.getProvider().getId());
        }

        String token = jwtUtil.generateToken(authentication.getName(), MemberRole.Normal);

        System.out.println("name : " + newUserDTO.getName());

        // 리다이렉트할 URL 설정
        String redirectUrl = userService.getHost_url() + "?token=" + token;
        System.out.println("HOST_URL : "+ redirectUrl);
        // 리다이렉트
        response.sendRedirect(redirectUrl);
    }
}
