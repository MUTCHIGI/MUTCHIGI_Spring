package com.CAUCSD.MUTCHIGI.user.provider;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
public class Provider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique=true)
    private String providerName;
}
