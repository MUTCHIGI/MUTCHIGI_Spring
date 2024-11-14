package com.CAUCSD.MUTCHIGI.user;

import com.CAUCSD.MUTCHIGI.user.provider.Provider;
import com.CAUCSD.MUTCHIGI.user.provider.ProviderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProviderRepository providerRepository;

    public String getHost_url() {
        return host_url;
    }

    @Value("${front.host.url.testSet}")
    private String host_url;

    public UserEntity registerUser(UserDTO userDTO) {
        UserEntity userEntity = userRepository.findByEmail(userDTO.getEmail());
        if (userEntity == null) {
            userEntity = new UserEntity();

            userEntity.setPlatformUserId(userDTO.getPlatformUserId());
            userEntity.setEmail(userDTO.getEmail());
            userEntity.setName(userDTO.getName());
            userEntity.setProfileImageURL(userDTO.getProfileImageURL());
            userEntity.setRole(MemberRole.Normal);

            Provider provider = providerRepository.findById(userDTO.getProviderId()).orElse(null);
            if (provider == null) {
                throw new IllegalArgumentException("Provider not found for ID: " + userDTO.getProviderId());
            }
            userEntity.setProvider(provider);

            return userRepository.save(userEntity);
        }
        return userEntity;
    }

}
