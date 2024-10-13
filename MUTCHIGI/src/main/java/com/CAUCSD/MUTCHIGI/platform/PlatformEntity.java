package com.CAUCSD.MUTCHIGI.platform;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class PlatformEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int platformId;

    @Column(unique=true)
    private String platformName;

    @Column(unique=true)
    private Byte platformType;
    // 1 = SNS 로그인 , 2 = 노래 플랫폼 ID
}
