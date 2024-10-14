package com.CAUCSD.MUTCHIGI.song;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
public class YoutubeSongDTO {

    private long songId;
    private String songPlatformId;
    private String songName;
    private String singerName;
    private String playURL;
    private String thumbnailURL;

    private LocalTime startTime;

}
