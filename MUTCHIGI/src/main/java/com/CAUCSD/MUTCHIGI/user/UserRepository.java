package com.CAUCSD.MUTCHIGI.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    UserEntity findByEmail(String email);

    UserEntity findByPlatformUserId(String platformUserId);
}
