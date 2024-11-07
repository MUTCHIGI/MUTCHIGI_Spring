package com.CAUCSD.MUTCHIGI.song.demucs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/GCP")
public class GCPPubSubController {

    @Autowired
    private GCPPubSubService gcpPubSubService;

    @PostMapping("/publish")
    public String publishMessage(
            @RequestParam String youtubeURL,
            @RequestParam long songId
    ){
        return gcpPubSubService.publicMessage(youtubeURL, songId);
    }
}
