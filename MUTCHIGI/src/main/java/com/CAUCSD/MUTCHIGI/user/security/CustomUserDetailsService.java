package com.CAUCSD.MUTCHIGI.user.security;


import com.CAUCSD.MUTCHIGI.user.UserController;
import com.CAUCSD.MUTCHIGI.user.UserEntity;
import com.CAUCSD.MUTCHIGI.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    public UserController userController;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByPlatformUserId(username);

        if (userEntity == null) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없음 : " + username);
        }

        return new org.springframework.security.core.userdetails.User(
                userEntity.getEmail(),
                "",
                new ArrayList<>()
        );
    }
}
