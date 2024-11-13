package com.CAUCSD.MUTCHIGI.song.demucs;

import lombok.Data;

import java.time.LocalTime;

@Data
public class MyDemucsSongDTO {
    private long songId;
    private String songPlatformId;
    private String songName;
    private String singerName;
    private String playURL;
    private String thumbnailURL;
    private LocalTime songTime;
    private String messageId;
    private boolean demucsCompleted;
}
