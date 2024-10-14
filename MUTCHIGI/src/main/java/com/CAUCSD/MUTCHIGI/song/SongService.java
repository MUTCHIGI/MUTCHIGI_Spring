package com.CAUCSD.MUTCHIGI.song;

import com.CAUCSD.MUTCHIGI.quiz.QuizRepository;
import com.CAUCSD.MUTCHIGI.quiz.QuizService;
import com.CAUCSD.MUTCHIGI.quizSong.QuizSongRelation;
import com.CAUCSD.MUTCHIGI.quizSong.QuizSongRelationReopository;
import com.CAUCSD.MUTCHIGI.song.singer.SingerEntity;
import com.CAUCSD.MUTCHIGI.song.singer.SingerRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SongService {

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private SingerRepository singerRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuizSongRelationReopository quizSongRelationReopository;

    @Value("${youtube.api,key}")
    private String youtubeAPIKey;

    private String baseYoutubeURL = "https://www.googleapis.com/youtube/v3/videos";

    public YoutubeSongDTO getYoutubeSong(String youtubeURL, long quizId) {
        String videoId = extractYoutubeVideoId(youtubeURL);
        if (videoId == null) {
            return null;
        }

        String apiURL = String.format("%s?id=%s&key=%s&part=snippet,contentDetails", baseYoutubeURL, videoId, youtubeAPIKey);
        RestTemplate restTemplate = new RestTemplate();
        String apiResponse = restTemplate.getForObject(apiURL, String.class);

        return mapToYoutubeSong(apiResponse, quizId);
    }

    private String extractYoutubeVideoId(String youtubeURL) {
        String regex = "(?<=v=|/|be/)([a-zA-Z0-9_-]{11})";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(youtubeURL);
        return matcher.find() ? matcher.group(0) : null;
    }

    private YoutubeSongDTO mapToYoutubeSong(String apiResponse, long quizId) {
        try {
            JsonNode rootNode = new ObjectMapper().readTree(apiResponse);
            JsonNode itemNode = rootNode.path("items").get(0);

            if(itemNode == null) {
                return null;
            }

            SongEntity songEntity = new SongEntity();

            //기존에 가수 있는 경우 처리해야 함.
            SingerEntity singerEntity = new SingerEntity();
            singerEntity.setSingerName(itemNode.path("snippet").path("channelTitle").asText());
            singerEntity = singerRepository.save(singerEntity);

            //기존에 노래 있는 경우 처리해야 함.
            songEntity.setSongPlatformId(itemNode.path("id").asText());
            songEntity.setSongName(itemNode.path("snippet").path("title").asText());
            songEntity.setPlayURL("https://www.youtube.com/watch?v=" + itemNode.path("id").asText());
            songEntity.setThumbnailURL(itemNode.path("snippet").path("thumbnails").path("url").asText());

            String duration = itemNode.path("contentDetails").path("duration").asText();

            songEntity = songRepository.save(songEntity);
            QuizSongRelation quizSongRelation = new QuizSongRelation();
            quizSongRelation.setSongEntity(songEntity);
            quizSongRelation.setQuizEntity(quizRepository.findById(quizId).get());
            quizSongRelation.setStartTime(durationToLocalTime(duration));

            YoutubeSongDTO youtubeSongDTO = new YoutubeSongDTO();
            youtubeSongDTO.setSongId(songEntity.getSongId());
            youtubeSongDTO.setSongPlatformId(songEntity.getSongPlatformId());
            youtubeSongDTO.setSongName(songEntity.getSongName());
            youtubeSongDTO.setSingerName(singerEntity.getSingerName());
            youtubeSongDTO.setPlayURL(songEntity.getPlayURL());
            youtubeSongDTO.setThumbnailURL(songEntity.getThumbnailURL());
            youtubeSongDTO.setStartTime(quizSongRelation.getStartTime());

            songEntity.setSongName(youtubeSongDTO.getSongName());

            return youtubeSongDTO;

        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private LocalTime durationToLocalTime(String duration) {
        System.out.println("테스트입니다 : " + duration);

        String time = duration.replace("PT", "")
                .replace("H",":")
                .replace("M",":")
                .replace("S","");

        String[] parts = time.split(":");
        int hmsLength = parts.length;
        LocalTime startTime = LocalTime.of(0, 0, 0);
        // 기본 시작 시간 0분 0초

        switch (hmsLength) {
            case 3:
                startTime = LocalTime.of(Integer.parseInt(parts[0]),Integer.parseInt(parts[1]),Integer.parseInt(parts[2]));
                break;
            case 2:
                startTime = LocalTime.of(0,Integer.parseInt(parts[0]),Integer.parseInt(parts[1]));
                break;
            case 1:
                startTime = LocalTime.of(0,0,Integer.parseInt(parts[0]));
        }
        return startTime;
    }


}
