package com.CAUCSD.MUTCHIGI.song;

import com.CAUCSD.MUTCHIGI.quiz.QuizEntity;
import com.CAUCSD.MUTCHIGI.quiz.QuizRepository;
import com.CAUCSD.MUTCHIGI.quizSong.answer.*;
import com.CAUCSD.MUTCHIGI.quizSong.hint.*;
import com.CAUCSD.MUTCHIGI.quizSong.QuizSongRelation;
import com.CAUCSD.MUTCHIGI.quizSong.QuizSongRelationReopository;
import com.CAUCSD.MUTCHIGI.song.singer.SingerEntity;
import com.CAUCSD.MUTCHIGI.song.singer.SingerRepository;
import com.CAUCSD.MUTCHIGI.song.singer.relation.SingerSongRelation;
import com.CAUCSD.MUTCHIGI.song.singer.relation.SingerSongRelationRepository;
import com.CAUCSD.MUTCHIGI.user.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SongService {

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private SingerRepository singerRepository;

    @Autowired
    private UserRepository userRepository;

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

    @Autowired
    private HintStateRepository hintStateRepository;

    @Value("${openai.model}")
    private String model;

    @Value("${openai.api.url}")
    private String apiURL;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${youtube.api,key}")
    private String youtubeAPIKey;

    private String baseYoutubeURL = "https://www.googleapis.com/youtube/v3/videos";

    private String baseYoutubeListURL = "https://www.googleapis.com/youtube/v3/playlistItems";

    private String prompt =  """
        Please return a JSON array of unique candidates based on the actual music title extracted from the YouTube music video title I provide. Follow these instructions.\s
        # basic rule\s
        if singer name included in front of title than exclude singer name\s
        number of candidates is limited to 10 or less without duplicates\s
        do not numbering to candidates\s
        do not add any special sign\s
        # specific rule
        ## If the music title is in English\s
        include the original title and both uppercase and lowercase versions, candidates of Korean transcriptions. \s
        Each transcription should closely match standard pronunciation and feel natural, focusing on minor variations such as vowels (e.g., ㅔ & ㅐ),\s
        ## If the music title is in Korean\s
        include candidates with removing spacing variations between words.\s
        ## If the music title is another languages
        include the original title and both uppercase and lowercase versions, candidates of Korean transcriptions. \s
        Each transcription should closely match standard pronunciation and feel natural, focusing on minor variations such as vowels (e.g., ㅔ & ㅐ),\s
    """;
    @Autowired
    private RestTemplate getRestTemplate;

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

        QuizEntity quizEntity = quizRepository.findById(myPlayListQuizDTO.getQuizId()).orElse(null);
        if (quizEntity == null) {
            return null;
        }
        if(quizEntity.getTypeId() == 2){
            if(videoIds.size() >= 10) videoIds = videoIds.subList(0, 10);
        }

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

        // QuizSongRelation 조회
        QuizSongRelation quizSongRelation = quizSongRelationRepository.findById(qsRelationId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 QuizSongRelation이 존재하지 않습니다."));

        // AnswerEntity 목록 조회
        List<AnswerEntity> answerEntityList = answerRepository.findByQuizSongRelation(quizSongRelation);

        // answerList의 크기 검증
        if (answerList.size() > 20) {
            throw new IllegalArgumentException("답변 목록의 크기가 20을 초과할 수 없습니다.");
        }

        // 각 AnswerEntity에 대해 답변 세팅
        for (int i = 0; i < answerEntityList.size() && i < answerList.size(); i++) {
            AnswerEntity getDBAnswerEntity = answerEntityList.get(i);
            getDBAnswerEntity.setAnswer(answerList.get(i));
            getDBAnswerEntity.setLLMUsed(false);
            idList.add(getDBAnswerEntity.getAnswerId());
        }

        // 남아 있는 AnswerEntity 제거
        if (answerList.size() < answerEntityList.size()) {
            for (int i = answerList.size(); i < answerEntityList.size(); i++) {
                answerRepository.delete(answerEntityList.get(i)); // 삭제
            }
            answerRepository.saveAll(answerEntityList.subList(0, answerList.size()));
        }
        if(answerList.size() >= answerEntityList.size()){
            for(int i = answerEntityList.size(); i < answerList.size(); i++){
                AnswerEntity overAnswerEntity = new AnswerEntity();
                overAnswerEntity.setQuizSongRelation(quizSongRelation);
                overAnswerEntity.setAnswer(answerList.get(i));
                overAnswerEntity.setLLMUsed(false);
                overAnswerEntity =  answerRepository.save(overAnswerEntity);
                idList.add(overAnswerEntity.getAnswerId());
            }
            answerRepository.saveAll(answerEntityList.subList(0, answerEntityList.size()));
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

    public LocalTime getStartTime(long qsRelationId){
        QuizSongRelation quizSongRelation = quizSongRelationRepository.findById(qsRelationId).orElse(null);
        if(quizSongRelation == null){
            return null;
        }
        return quizSongRelation.getStartTime();
    }

    public Integer getHintCountFromDB(long quizId){
        QuizEntity quizEntity = quizRepository.findById(quizId).orElse(null);
        List<HintStateEntity> hintStateEntityList = hintStateRepository.findByQuizEntity(quizEntity);

        quizEntity.setHintCount(hintStateEntityList.size());
        quizRepository.save(quizEntity);

        return hintStateEntityList.size();
    }

    public List<Long> saveHintList(List<HintDTO> hintDTOList, long qsRelationId){
        List<Long> hintIdList = new ArrayList<>();
        List<HintEntity> hintEntities =
                hintRepository.findByQuizSongRelation(
                        quizSongRelationRepository.findById(
                                qsRelationId).orElse(null));

        for(int i =0; i < hintDTOList.size(); i++){
            HintEntity hintEntity;
            if(hintEntities.size() <= i){ // DB에 저장된 힌트가 없으면
                hintEntity = new HintEntity();
            }
            else{
                hintEntity = hintEntities.get(i);
            }
            HintStateEntity hintStateEntity = hintStateRepository.findById(hintDTOList.get(i).getHintStateId()).orElse(null);

            LocalTime hintTime = hintStateEntity.getHintTime();

            hintEntity.setHintTime(hintTime);
            hintEntity.setHintType(hintStateEntity.getHintType());
            hintEntity.setHintText(hintDTOList.get(i).getHintText());
            hintEntity.setQuizSongRelation(quizSongRelationRepository.findById(qsRelationId).orElse(null));
            hintEntity = hintRepository.save(hintEntity);

            hintIdList.add(hintEntity.getHintId());
        }
        for(HintEntity hint : hintEntities){
            if(!hintIdList.contains(hint.getHintId())){
                hintRepository.delete(hint);
            }
        }

        return hintIdList;
    }

    public List<GetHintDTO> getHintList(long qsRelationId){
        QuizSongRelation quizSongRelation = quizSongRelationRepository.findById(qsRelationId).orElse(null);
        List<HintEntity> hintEntities = hintRepository.findByQuizSongRelation(quizSongRelation);
        List<GetHintDTO> hintDTOList = new ArrayList<>();
        for(HintEntity hintEntity : hintEntities){
            GetHintDTO getHintDTO = new GetHintDTO();
            getHintDTO.setHintType(hintEntity.getHintType());
            getHintDTO.setHintText(hintEntity.getHintText());
            getHintDTO.setHintId(hintEntity.getHintId());
            getHintDTO.setHintTime(hintEntity.getHintTime());
            hintDTOList.add(getHintDTO);
        }
        return hintDTOList;
    }


    public Void deleteYoutubeSongDB(long qsRelationId){
        QuizSongRelation deleteQSRelation = quizSongRelationRepository.findById(qsRelationId).orElse(null);
        if(deleteQSRelation != null){
            List<AnswerEntity> deleteAnswerList = answerRepository.findByQuizSongRelation(deleteQSRelation);
            answerRepository.deleteAll(deleteAnswerList);
            List<HintEntity> deleteHintList = hintRepository.findByQuizSongRelation(deleteQSRelation);
            hintRepository.deleteAll(deleteHintList);
            quizSongRelationRepository.deleteById(qsRelationId);
        }
        QuizEntity quizEntity = deleteQSRelation.getQuizEntity();
        quizEntity.setSongCount(quizEntity.getSongCount() - 1);
        quizRepository.save(quizEntity);

        // 삭제 후 확인
        if (!quizSongRelationRepository.existsById(qsRelationId)) {
            return null;
            // 삭제가 성공적으로 이루어졌음
        } else {
            throw new EntityNotFoundException("ID가 " + qsRelationId + "인 항목을 찾을 수 없습니다.");
            // 삭제에 실패했거나 항목이 존재하지 않음
        }
    }

    public List<YoutubeSongDTO> getYoutubeSongDTOList(long quizId){
        List<QuizSongRelation> quizSongRelations = quizSongRelationRepository.findByQuizEntity_QuizId(quizId);

        List<YoutubeSongDTO> youtubeSongDTOList = new ArrayList<>();

        for(QuizSongRelation quizSongRelation : quizSongRelations){
            SongEntity songEntity = quizSongRelation.getSongEntity();
            SingerSongRelation singerSongRelation = singerSongRelationRepository.findBySong(songEntity);
            SingerEntity singerEntity = singerSongRelation.getSinger();
            if(songEntity != null){
                youtubeSongDTOList.add(getYoutubeSongDTO(songEntity, singerEntity, quizSongRelation));
            }
        }

        return youtubeSongDTOList;
    }

    public List<String> getAnswerFromDBwithGPT(long qsRelationId){
        QuizSongRelation quizSongRelation = quizSongRelationRepository.findById(qsRelationId).orElse(null);
        if(quizSongRelation == null){
            return null;
        }
        List<AnswerEntity> answerEntityList = answerRepository.findByQuizSongRelation(quizSongRelation);
        List<String> gptAnswers = new ArrayList<>();
        if(answerEntityList.isEmpty()){
            String songTitle = quizSongRelation.getSongEntity().getSongName();
            String updatedPrompt = prompt + "The title is " + songTitle;

            // response_format 설정
            Map<String, Object> responseFormat = new HashMap<>();
            responseFormat.put("type", "json_schema");
            responseFormat.put("json_schema", Map.of(
                    "name", "answer_schema",
                    "schema", Map.of(
                            "type", "object",
                            "properties", Map.of(
                                    "answer", Map.of(
                                            "description", "The answers that appears in the input",
                                            "type", "array",
                                            "items", Map.of("type", "string")
                                    )
                            ),
                            "required", List.of("answer"),
                            "additionalProperties", false
                    )
            ));

            GPTRequest gptRequest = new GPTRequest(model, updatedPrompt, responseFormat);
            Map<String, Object> gptResponse = getRestTemplate.postForObject(apiURL, gptRequest, Map.class);

            //System.out.println(gptResponse);

            try {
                // 응답 구조에서 answer 추출
                if (gptResponse != null) {
                    List<Map<String, Object>> choices = (List<Map<String, Object>>) gptResponse.get("choices");
                    if (choices != null && !choices.isEmpty()) {
                        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                        if (message != null && message.containsKey("content")) {
                            // content를 JSON으로 파싱
                            Map<String, Object> content = new ObjectMapper().readValue((String) message.get("content"), Map.class);
                            if (content.containsKey("answer")) {
                                gptAnswers.addAll ((List<String>) content.get("answer"));
                            }
                        }
                    }
                    List<AnswerEntity> gptAnswerEntityList = new ArrayList<>();
                    for(String answer : gptAnswers){
                        AnswerEntity answerEntity = new AnswerEntity();
                        answerEntity.setAnswer(answer);
                        answerEntity.setLLMUsed(true);
                        answerEntity.setQuizSongRelation(quizSongRelation);
                        gptAnswerEntityList.add(answerEntity);
                    }
                    answerRepository.saveAll(gptAnswerEntityList);


                }else{
                    return null;
                }
            }catch (JsonProcessingException e){
                e.printStackTrace();
                return null;
            }
        }else{
            for(AnswerEntity answerEntity : answerEntityList){
                gptAnswers.add(answerEntity.getAnswer());
            }
        }

        return gptAnswers;
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
            QuizEntity quizEntity = quizRepository.findById(quizId).orElse(null);

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
                songEntity.setDemucsCompleted(false);
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
                return null;
            }

            if(existSinger == null || existPlatformSong == null){
                SingerSongRelation singerSongRelation = new SingerSongRelation();
                singerSongRelation.setSong(songEntity);
                singerSongRelation.setSinger(singerEntity);
                singerSongRelationRepository.save(singerSongRelation);
            }

            if(quizEntity != null){
                quizEntity.setSongCount(quizEntity.getSongCount() + 1);
                quizRepository.save(quizEntity);
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
        if(quizSongRelation != null){
            youtubeSongDTO.setQuizSongRelationID(quizSongRelation.getQSRelationId());
        }
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
