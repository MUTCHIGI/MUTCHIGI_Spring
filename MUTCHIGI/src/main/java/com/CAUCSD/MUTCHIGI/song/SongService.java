package com.CAUCSD.MUTCHIGI.song;

import com.CAUCSD.MUTCHIGI.quiz.QuizEntity;
import com.CAUCSD.MUTCHIGI.quiz.QuizRepository;
import com.CAUCSD.MUTCHIGI.quiz.QuizService;
import com.CAUCSD.MUTCHIGI.quiz.hint.HintDTO;
import com.CAUCSD.MUTCHIGI.quiz.hint.HintEntity;
import com.CAUCSD.MUTCHIGI.quiz.hint.HintRepository;
import com.CAUCSD.MUTCHIGI.quizSong.QuizSongRelation;
import com.CAUCSD.MUTCHIGI.quizSong.QuizSongRelationReopository;
import com.CAUCSD.MUTCHIGI.song.answer.AnswerEntity;
import com.CAUCSD.MUTCHIGI.song.answer.AnswerRepository;
import com.CAUCSD.MUTCHIGI.song.singer.SingerEntity;
import com.CAUCSD.MUTCHIGI.song.singer.SingerRepository;
import com.CAUCSD.MUTCHIGI.song.singer.relation.SingerSongRelation;
import com.CAUCSD.MUTCHIGI.song.singer.relation.SingerSongRelationRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
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
    private QuizSongRelationReopository quizSongRelationRepository;

    @Autowired
    private SingerSongRelationRepository singerSongRelationRepository;

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private HintRepository hintRepository;


    @Value("${youtube.api,key}")
    private String youtubeAPIKey;

    private String baseYoutubeURL = "https://www.googleapis.com/youtube/v3/videos";

    private String baseYoutubeListURL = "https://www.googleapis.com/youtube/v3/playlistItems";

    public YoutubeSongDTO getYoutubeSong(String youtubeURL, long quizId) {
        String videoId = extractYoutubeVideoId(youtubeURL);
        if (videoId == null) {
            return null;
        }

        String apiURL = String.format("%s?id=%s&key=%s&part=snippet,contentDetails", baseYoutubeURL, videoId, youtubeAPIKey);
        RestTemplate restTemplate = new RestTemplate();
        String apiResponse = restTemplate.getForObject(apiURL, String.class);

        // API 응답을 JsonNode로 파싱
        try {
            JsonNode rootNode = new ObjectMapper().readTree(apiResponse);
            JsonNode itemNode = rootNode.path("items").get(0); // 첫 번째 항목 가져오기

            if (itemNode == null) {
                return null;
            }

            // itemNode를 mapToYoutubeSong 메소드에 전달
            return mapToYoutubeSong(itemNode, quizId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<YoutubeSongDTO> addMyPlayListToQuiz(MyPlayListQuizDTO myPlayListQuizDTO){
        String playListId = extractPlaylistId(myPlayListQuizDTO.getMyPlayListURL());
        if(playListId == null){
            return new ArrayList<>();
        }

        List<String> videoIds = new ArrayList<>();
        String nextPageToken = null;

        do{
            String apiURL = String.format("%s?playlistId=%s&key=%s&part=snippet&maxResults=50&pageToken=%s"
                    , baseYoutubeListURL, playListId, youtubeAPIKey, nextPageToken != null ? nextPageToken : "");

            RestTemplate restTemplate = new RestTemplate();
            String apiResponse = restTemplate.getForObject(apiURL, String.class);
            //System.out.println("플리 api 응답 : "+apiResponse);

            videoIds.addAll(extractVideoIdInListResponse(apiResponse));
            //System.out.println("비디오 ID : "+ videoIds);

            nextPageToken = extractNextPageToken(apiResponse);

            // 다음 페이지 토큰이 null이거나 빈 문자열이면 루프 종료
            if (nextPageToken == null || nextPageToken.isEmpty()) {
                break;
            }
        }while (true);

        List<YoutubeSongDTO> songs = new ArrayList<>();
        for(String videoId : videoIds){
            String apiURL = String.format("%s?id=%s&key=%s&part=snippet,contentDetails", baseYoutubeURL, videoId, youtubeAPIKey);
            RestTemplate restTemplate = new RestTemplate();
            String apiResponse = restTemplate.getForObject(apiURL, String.class);
            // API 응답을 JsonNode로 파싱
            try {
                JsonNode rootNode = new ObjectMapper().readTree(apiResponse);
                JsonNode itemNode = rootNode.path("items").get(0); // 첫 번째 항목 가져오기

                if (itemNode == null) {
                    return null;
                }

                // itemNode를 mapToYoutubeSong 메소드에 전달
                songs.add(mapToYoutubeSong(itemNode, myPlayListQuizDTO.getQuizId()));
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

        }
        return songs;
    }

    public List<Long> saveYoutubeAnswer(List<String> answerList, long qsRelationId){
        List<Long> idList = new ArrayList<>();

        for(String answer : answerList){
            AnswerEntity answerEntity = new AnswerEntity();
            QuizSongRelation quizSongRelation = quizSongRelationRepository.findById(qsRelationId).orElse(null);
            if(quizSongRelation == null){
                return null;
            }
            answerEntity.setAnswer(answer);
            answerEntity.setQuizSongRelation(quizSongRelation);
            answerEntity.setLLMUsed(false);
            answerEntity = answerRepository.save(answerEntity);
            idList.add(answerEntity.getAnswerId());
        }

        if(idList.size() > 20){
            throw new IllegalArgumentException("답변 목록의 크기가 20을 초과할 수 없습니다.");
        }
        return idList;
    }

    public LocalTime saveStartTime(TimeDTO timeDTO, long qsRelationId){
        QuizSongRelation quizSongRelation = quizSongRelationRepository.findById(qsRelationId).orElse(null);
        if(quizSongRelation == null){
            return null;
        }
        LocalTime startTime = LocalTime.of(timeDTO.getHour(), timeDTO.getMinute(), timeDTO.getSecond());
        quizSongRelation.setStartTime(startTime);
        quizSongRelation = quizSongRelationRepository.save(quizSongRelation);

        return quizSongRelation.getStartTime();

    }

    public Integer getHintCountFromDB(long quizId){
        QuizEntity quizEntity = quizRepository.findById(quizId).orElse(null);

        return quizEntity.getHintCount();
    }

    public List<Long> saveHintList(List<HintDTO> hintDTOList, long qsRelationId){
        List<Long> hintIdList = new ArrayList<>();

        for(HintDTO hintDTO : hintDTOList){
            HintEntity hintEntity = new HintEntity();
            LocalTime hintTime = LocalTime.of(hintDTO.getHour(), hintDTO.getMinute(), hintDTO.getSecond());

            hintEntity.setHintTime(hintTime);
            hintEntity.setHintType(hintDTO.getHintType());
            hintEntity.setHintText(hintDTO.getHintText());
            hintEntity.setQuizSongRelation(quizSongRelationRepository.findById(qsRelationId).orElse(null));
            hintEntity = hintRepository.save(hintEntity);

            hintIdList.add(hintEntity.getHintId());
        }
        return hintIdList;
    }




    private String extractYoutubeVideoId(String youtubeURL) {
        String regex = "(?<=v=|/|be/)([a-zA-Z0-9_-]{11})";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(youtubeURL);
        return matcher.find() ? matcher.group(0) : null;
    }

    private String extractPlaylistId(String playlistUrl) {
        String regex = "(?<=list=|/)([a-zA-Z0-9_-]{34})";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(playlistUrl);
        return matcher.find() ? matcher.group(0) : null;
    }

    /*
    private List<YoutubeSongDTO> mapToYoutubeSongs(String apiResponse, long quizId){
        List<YoutubeSongDTO> mappedSongs = new ArrayList<>();

        try {
            JsonNode rootNode = new ObjectMapper().readTree(apiResponse);
            JsonNode items = rootNode.get("items");

            for(JsonNode item : items){
                YoutubeSongDTO youtubeSongDTO = mapToYoutubeSong(item, quizId);
                if(youtubeSongDTO != null){
                    mappedSongs.add(youtubeSongDTO);
                }
                System.out.println("각각 : "+youtubeSongDTO);
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return mappedSongs;
    }

     */

    private List<String> extractVideoIdInListResponse(String apiResponse) {
        List<String> videoIdList = new ArrayList<>();
        try {
            JsonNode rootNode = new ObjectMapper().readTree(apiResponse);
            JsonNode itemNodes = rootNode.path("items");

            if(itemNodes != null && itemNodes.isArray()){
                for(JsonNode itemNode : itemNodes){
                    String videoId = itemNode.path("snippet").path("resourceId").path("videoId").asText();
                    if(!videoId.isEmpty()){
                        videoIdList.add(videoId);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return videoIdList;
    }

    private YoutubeSongDTO mapToYoutubeSong(JsonNode itemNode, long quizId) {
        try {
            //기존에 가수 있는 경우 처리 완료
            SingerEntity singerEntity;
            String singerName = itemNode.path("snippet").path("channelTitle").asText();
            SingerEntity existSinger = singerRepository.findBySingerName(singerName);

            if(existSinger!=null) {
                singerEntity = existSinger;
            }else{
                singerEntity = new SingerEntity();
                singerEntity.setSingerName(singerName);
                singerEntity = singerRepository.save(singerEntity);
            }


            String duration = itemNode.path("contentDetails").path("duration").asText();

            //기존에 노래 있는 경우 처리해야 함.
            SongEntity songEntity;
            String existSongPlatformID = itemNode.path("id").asText();
            SongEntity existPlatformSong = songRepository.findBySongPlatformId(existSongPlatformID);

            if(existPlatformSong != null){
                songEntity = existPlatformSong;
            }else{
                songEntity = new SongEntity();
                songEntity.setSongPlatformId(existSongPlatformID);
                songEntity.setSongName(itemNode.path("snippet").path("title").asText());
                songEntity.setPlayURL("https://www.youtube.com/watch?v=" + itemNode.path("id").asText());
                songEntity.setThumbnailURL(itemNode.path("snippet").path("thumbnails").path("standard").path("url").asText());
                songEntity.setSongTime(durationToLocalTime(duration));
                songEntity = songRepository.save(songEntity);
            }

            //중복 제거
            QuizSongRelation quizSongRelation;
            QuizSongRelation existQSRelation = quizSongRelationRepository.findByQuizEntity_QuizIdAndSongEntity_SongId(quizId, songEntity.getSongId());
            if(existQSRelation == null) {
                quizSongRelation = new QuizSongRelation();
                quizSongRelation.setSongEntity(songEntity);
                quizSongRelation.setQuizEntity(quizRepository.findById(quizId).get());
                quizSongRelationRepository.save(quizSongRelation);
            }else{
                quizSongRelation = existQSRelation;
            }

            if(existSinger == null || existPlatformSong == null){
                SingerSongRelation singerSongRelation = new SingerSongRelation();
                singerSongRelation.setSong(songEntity);
                singerSongRelation.setSinger(singerEntity);
                singerSongRelationRepository.save(singerSongRelation);
            }

            return getYoutubeSongDTO(songEntity, singerEntity, quizSongRelation);

        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static YoutubeSongDTO getYoutubeSongDTO(SongEntity songEntity, SingerEntity singerEntity, QuizSongRelation quizSongRelation) {
        YoutubeSongDTO youtubeSongDTO = new YoutubeSongDTO();
        youtubeSongDTO.setSongId(songEntity.getSongId());
        youtubeSongDTO.setSongPlatformId(songEntity.getSongPlatformId());
        youtubeSongDTO.setSongName(songEntity.getSongName());
        youtubeSongDTO.setSingerName(singerEntity.getSingerName());
        youtubeSongDTO.setPlayURL(songEntity.getPlayURL());
        youtubeSongDTO.setThumbnailURL(songEntity.getThumbnailURL());
        youtubeSongDTO.setSongTime(songEntity.getSongTime());
        youtubeSongDTO.setQuizSongRelationID(quizSongRelation.getQSRelationId());
        return youtubeSongDTO;
    }

    private LocalTime durationToLocalTime(String duration) {
        //System.out.println("테스트입니다 : " + duration);

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

    private String extractNextPageToken(String apiResponse) {
        try {
            JsonNode rootNode = new ObjectMapper().readTree(apiResponse);
            return rootNode.path("nextPageToken").asText(null);  // null을 기본값으로 설정
        } catch (Exception e) {
            e.printStackTrace();
            return null;  // 파싱 중 오류 발생 시 null 반환
        }
    }


}
