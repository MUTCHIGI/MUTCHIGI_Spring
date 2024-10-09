package com.CAUCSD.MUTCHIGI.user.provider;

import jakarta.persistence.*;

@Entity
public class provider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique=true)
    private String provider_name;
}
