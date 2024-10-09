package com.CAUCSD.MUTCHIGI.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(unique = true)
    private String platformUserId;

    @Column(unique = true)
    private String email;

    private String name;

    private String profileImageURL;

    @Enumerated(EnumType.STRING)
    private MemberRole role;

    // provider : google이 들어감
    private int providerId;

}
