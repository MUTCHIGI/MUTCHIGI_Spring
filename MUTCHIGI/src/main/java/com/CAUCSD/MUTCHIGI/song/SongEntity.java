package com.CAUCSD.MUTCHIGI.song;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Setter
@Getter
@Entity
public class SongEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long songId;

    private String songName;

    private LocalTime startTime;

    private long singerId;

    private String playURL;

    private String thumbnailURL;
}
