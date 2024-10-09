package com.CAUCSD.MUTCHIGI.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public UserEntity registerUser(UserDTO userDTO) {
        UserEntity userEntity = userRepository.findByEmail(userDTO.getEmail());
        if (userEntity == null) {
            userEntity = new UserEntity();

            userEntity.setPlatformUserId(userDTO.getPlatform_user_id());
            userEntity.setEmail(userDTO.getEmail());
            userEntity.setName(userDTO.getName());
            userEntity.setProfileImageURL(userDTO.getProfileImageURL());
            userEntity.setRole(MemberRole.Normal);
            userEntity.setProviderId(userDTO.getProvider_id());

            return userRepository.save(userEntity);
        }
        return userEntity;
    }

}
