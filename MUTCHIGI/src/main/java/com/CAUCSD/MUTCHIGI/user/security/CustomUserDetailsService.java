package com.CAUCSD.MUTCHIGI.user.security;


import com.CAUCSD.MUTCHIGI.user.UserEntity;
import com.CAUCSD.MUTCHIGI.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByPlatformUserId(username);

        if (userEntity == null) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없음 : " + username);
        }

        GrantedAuthority authority = new SimpleGrantedAuthority(userEntity.getRole().name());

        return new org.springframework.security.core.userdetails.User(
                userEntity.getEmail(),
                "",
                Collections.singletonList(authority)
        );
    }
}
