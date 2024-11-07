package com.CAUCSD.MUTCHIGI.song.demucs;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class GCPPubSubService {

    private final PubSubTemplate pubSubTemplate;

    @Value("${spring.cloud.gcp.topicPub}")
    private String topicPub;

    @Value("${spring.cloud.gcp.topicSub}")
    private String topicSub;


    public GCPPubSubService(PubSubTemplate pubSubTemplate) {
        this.pubSubTemplate = pubSubTemplate;
    }


    public String publicMessage(String youtubeURL, long songId){
        Map<String, String> message = new HashMap<>();
        message.put("youtube_url", youtubeURL);
        message.put("songId", String.valueOf(songId));

        System.out.println("youtube : "+ youtubeURL + "songID : " + songId);

        String messageData = String.format("{\"youtube_url\": \"%s\", \"songId\": \"%s\"}", youtubeURL, songId);
        String encodedData = Base64.getEncoder().encodeToString(messageData.getBytes()g);

        // 메시지 전체 JSON 구조 생성
        String jsonMessage = String.format("{\"messages\": [{\"data\": \"%s\"}]}", encodedData);

        CompletableFuture<String> future = pubSubTemplate.publish(topicPub, jsonMessage);

        try{
            return future.get();
        }catch (Exception e){
            e.printStackTrace();
            return "error";
        }



    }
}
