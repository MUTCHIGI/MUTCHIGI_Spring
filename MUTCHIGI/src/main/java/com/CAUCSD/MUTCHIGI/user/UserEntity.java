package com.CAUCSD.MUTCHIGI.user;

import com.CAUCSD.MUTCHIGI.user.provider.Provider;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long userId;

    @Column(unique = true)
    private String platformUserId;

    @Column(unique = true)
    private String email;

    private String name;

    private String profileImageURL;

    @Enumerated(EnumType.STRING)
    private MemberRole role;

    // provider : google이 들어감
   // private int providerId;

    @ManyToOne // Provider와의 관계 설정
    @JoinColumn(name = "provider_id", referencedColumnName = "id") // 외래 키 설정
    private Provider provider; // Provider 객체 추가

}
