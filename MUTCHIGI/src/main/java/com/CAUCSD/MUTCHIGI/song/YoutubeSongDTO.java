package com.CAUCSD.MUTCHIGI.song;

import lombok.*;

import java.time.LocalTime;

@Data
public class YoutubeSongDTO {

    private long songId;
    private String songPlatformId;
    private String songName;
    private String singerName;
    private String playURL;
    private String thumbnailURL;
    private LocalTime songTime;

    private long quizSongRelationID;

}
