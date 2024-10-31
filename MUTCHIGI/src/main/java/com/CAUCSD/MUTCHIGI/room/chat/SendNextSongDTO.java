package com.CAUCSD.MUTCHIGI.room.chat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendNextSongDTO {
    private long qsRelationId;
    private String songURL;
    private String originalSongURL;
    private String timeStamp;
}
