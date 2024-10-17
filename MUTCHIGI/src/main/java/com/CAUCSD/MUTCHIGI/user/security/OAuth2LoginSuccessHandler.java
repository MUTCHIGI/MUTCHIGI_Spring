package com.CAUCSD.MUTCHIGI.user.security;

import com.CAUCSD.MUTCHIGI.user.UserDTO;
import com.CAUCSD.MUTCHIGI.user.UserEntity;
import com.CAUCSD.MUTCHIGI.user.UserRepository;
import com.CAUCSD.MUTCHIGI.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import java.io.IOException;

@Configuration
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;

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

        String token = jwtUtil.generateToken(authentication.getName());

        System.out.println("name : " + newUserDTO.getName());

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);

        String jsonResponse = String.format("{\"token\": \"%s\", \"user\": %s}", token, new ObjectMapper().writeValueAsString(newUserDTO));
        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }
}
