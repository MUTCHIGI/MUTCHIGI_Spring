package com.CAUCSD.MUTCHIGI.song;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/song")
public class SongController {

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private SongService songService;

    @GetMapping("/youtube")
    public ResponseEntity<YoutubeSongDTO> youtube(
            @RequestParam String youtubeURL,
            @RequestParam long quizId
    ){
        return ResponseEntity.ok().body(songService.getYoutubeSong(youtubeURL, quizId));
    }

}
