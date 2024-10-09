package com.CAUCSD.MUTCHIGI.user;

import jakarta.persistence.*;

@Entity
public class simpleData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
}
