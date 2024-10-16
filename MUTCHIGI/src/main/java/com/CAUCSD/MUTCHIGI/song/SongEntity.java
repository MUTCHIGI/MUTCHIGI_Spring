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

    private String songPlatformId;

    private String songName;

    private String playURL;

    private LocalTime songTime;

    private String thumbnailURL;
}
