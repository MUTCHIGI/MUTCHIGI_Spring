package com.CAUCSD.MUTCHIGI.song.singer;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class SingerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long singerId;

    private String singerName;
}
