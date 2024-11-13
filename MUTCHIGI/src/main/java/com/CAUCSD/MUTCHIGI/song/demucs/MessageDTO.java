package com.CAUCSD.MUTCHIGI.song.demucs;

import lombok.Data;

import java.util.Map;

@Data
public class MessageDTO {
    private String status;
    private String song_id;
    private Map<String, String> download_links;
}
