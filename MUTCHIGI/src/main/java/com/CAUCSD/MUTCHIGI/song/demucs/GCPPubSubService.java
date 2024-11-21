package com.CAUCSD.MUTCHIGI.song.demucs;

import com.CAUCSD.MUTCHIGI.quiz.QuizEntity;
import com.CAUCSD.MUTCHIGI.quiz.QuizRepository;
import com.CAUCSD.MUTCHIGI.quizSong.QuizSongRelation;
import com.CAUCSD.MUTCHIGI.quizSong.QuizSongRelationReopository;
import com.CAUCSD.MUTCHIGI.song.SongEntity;
import com.CAUCSD.MUTCHIGI.song.SongRepository;
import com.CAUCSD.MUTCHIGI.song.SongService;
import com.CAUCSD.MUTCHIGI.song.singer.SingerEntity;
import com.CAUCSD.MUTCHIGI.song.singer.SingerRepository;
import com.CAUCSD.MUTCHIGI.song.singer.relation.SingerSongRelation;
import com.CAUCSD.MUTCHIGI.song.singer.relation.SingerSongRelationRepository;
import com.CAUCSD.MUTCHIGI.user.UserEntity;
import com.CAUCSD.MUTCHIGI.user.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.support.BasicAcknowledgeablePubsubMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class GCPPubSubService {

    @Value("${demucs.dir}")
    private String demucsLocalDir;

    @Value("${gcp.download.url}")
    private String baseDownloadUrl;

    @Autowired
    private PubSubTemplate pubSubTemplate;

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SingerRepository singerRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuizSongRelationReopository quizSongRelationRepository;

    @Autowired
    private SingerSongRelationRepository singerSongRelationRepository;

    @Value("${youtube.api,key}")
    private String youtubeAPIKey;

    private String baseYoutubeURL = "https://www.googleapis.com/youtube/v3/videos";
    @Autowired
    private SongService songService;

    public DemucsSongDTO publicMessage(String youtubeURL){
        String topicPub = "demucs";

        DemucsSongDTO demucsSongDTO =  getYoutubeSong(youtubeURL);
        long songId = demucsSongDTO.getSongId();

        if(demucsSongDTO.getSongTime().toSecondOfDay() < 6000){

            SongEntity songEntity = songRepository.findById(songId).orElse(null);
            if(songEntity == null){
                return demucsSongDTO;
            }
            else if(songEntity.isDemucsCompleted()){
                return demucsSongDTO;
            }

            Map<String, String> message = new HashMap<>();
            message.put("youtube_url", youtubeURL);
            message.put("songId", String.valueOf(songId));

            System.out.println("youtube : "+ youtubeURL + "songID : " + songId);

            try {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                String email = authentication.getName(); // 사용자명 추출

                System.out.println("Authenticated user: " + email);

                String jsonData = String.format("{\"youtube_url\": \"%s\", \"songId\": \"%s\"}", youtubeURL, songId);
                // JSON 문자열을 Base64로 인코딩

                // Base64로 인코딩된 데이터를 PubsubMessage에 추가
                CompletableFuture<String> messageId =  pubSubTemplate.publish(topicPub, jsonData);
                try{
                    String checkedMessageId  = messageId.get();
                    if(checkedMessageId != null){
                        if(songEntity != null){
                            UserEntity userEntity = userRepository.findByEmail(email);
                            songEntity.setMessageId(checkedMessageId);
                            songEntity.setUser(userEntity);
                            songEntity.setConvertOrderDate(LocalDateTime.now());
                            songRepository.save(songEntity);
                            demucsSongDTO.setMessageId(checkedMessageId);
                            return demucsSongDTO;
                        }
                    }

                }catch (Exception e){
                    throw new RuntimeException("Failed to publish message" , e);
                }

            }

            catch (Exception e){
                e.printStackTrace();
                return null;
            }
            return null;
        }else{
            return demucsSongDTO;
        }

    }

    // 메시지를 처리할 구독자 메서드
    @PostConstruct
    public void subscribeToTopic(){
        String topicSub = "demucs-download-sub";
        System.out.println("Subscribing to topic...");

        // Consumer<BasicAcknowledgeablePubsubMessage>로 메시지 수신
        Consumer<BasicAcknowledgeablePubsubMessage> messageReceiver = message -> {
            String payload = message.getPubsubMessage().getData().toStringUtf8();
            System.out.println("Received message: " + payload);
            downloadFilesFromMessage(payload);

            // 메시지 처리 후 ACK
            message.ack();
        };

        pubSubTemplate.subscribe(topicSub, messageReceiver);
    }

    public FileInputStream playDemucsSong(long songId, int instrumentId) throws IOException {
        String intrumentName ="";
        switch (instrumentId){
            case 1 :
                intrumentName = "vocals";
                break;
            case 2 :
                intrumentName = "bass";
                break;
            case 3 :
                intrumentName = "no_vocals";
                break;
            case 4 :
                intrumentName = "drums";
                break;
            default:
                throw new IllegalArgumentException("Invalid instrumentId: " + instrumentId);
        }
        String filePath = demucsLocalDir + songId + "_" + intrumentName + ".mp3";
        System.out.println("filePath는 : " + filePath);
        File file = new File(filePath);

        // 파일이 존재하는지 체크
        if (!file.exists()) {
            throw new FileNotFoundException("File not found: " + filePath);
        }

        return new FileInputStream(file);
    }

    public FileInputStream playDemucsSongInRoom(long qsRelationId) throws IOException {
        QuizSongRelation quizSongRelation = quizSongRelationRepository.findById(qsRelationId).orElse(null);
        if(quizSongRelation == null){
            return null;
        }
        int instrumentId = quizSongRelation.getQuizEntity().getInstrumentId();
        long songId = quizSongRelation.getSongEntity().getSongId();

        String intrumentName ="";
        switch (instrumentId){
            case 1 :
                intrumentName = "vocals";
                break;
            case 2 :
                intrumentName = "bass";
                break;
            case 3 :
                intrumentName = "no_vocals";
                break;
            case 4 :
                intrumentName = "drums";
                break;
            default:
                throw new IllegalArgumentException("Invalid instrumentId: " + instrumentId);
        }
        String filePath = demucsLocalDir + songId + "_" + intrumentName + ".mp3";

        System.out.println("filePath는 : " + filePath);
        File file = new File(filePath);

        // 파일이 존재하는지 체크
        if (!file.exists()) {
            throw new FileNotFoundException("File not found: " + filePath);
        }

        return new FileInputStream(file);
    }

    public List<DemucsSongDTO> getListDemucsSong(int page, int offset, String songTitle){
        PageRequest pageRequest = PageRequest.of(page, offset, Sort.by("convertOrderDate").descending());

        Page<SongEntity> songEntityPage = songRepository.findBySongNameContainingAndDemucsCompletedIsTrue(songTitle, pageRequest);

        return songEntityPage.getContent().stream()
                .map(songEntity -> {
                    DemucsSongDTO demucsSongDTO = new DemucsSongDTO();
                    demucsSongDTO.setSongId(songEntity.getSongId());
                    demucsSongDTO.setSongName(songEntity.getSongName());
                    demucsSongDTO.setSongPlatformId(songEntity.getSongPlatformId());
                    demucsSongDTO.setSongTime(songEntity.getSongTime());
                    demucsSongDTO.setPlayURL(songEntity.getPlayURL());
                    demucsSongDTO.setThumbnailURL(songEntity.getThumbnailURL());
                    demucsSongDTO.setMessageId(songEntity.getMessageId());
                    SingerSongRelation singerSongRelation = singerSongRelationRepository.findBySong(songEntity);
                    SingerEntity singerEntity = singerSongRelation.getSinger();
                    demucsSongDTO.setSingerName(singerEntity.getSingerName());
                    return demucsSongDTO;
                })
                .toList();

    }

    public List<MyDemucsSongDTO> getMyDemucsList(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        UserEntity userEntity = userRepository.findByEmail(email);
        List<SongEntity> songEntities = songRepository.findSongEntitiesByUserAndDemucsCompletedIsFalse(userEntity);

        return songEntities.stream().map(
                songEntity -> {
                    MyDemucsSongDTO myDemucsSongDTO = new MyDemucsSongDTO();
                    myDemucsSongDTO.setSongId(songEntity.getSongId());
                    myDemucsSongDTO.setSongPlatformId(songEntity.getSongPlatformId());
                    myDemucsSongDTO.setSongName(songEntity.getSongName());
                    SingerSongRelation singerSongRelation = singerSongRelationRepository.findBySong(songEntity);
                    SingerEntity singerEntity = singerSongRelation.getSinger();
                    myDemucsSongDTO.setSingerName(singerEntity.getSingerName());
                    myDemucsSongDTO.setPlayURL(songEntity.getPlayURL());
                    myDemucsSongDTO.setThumbnailURL(songEntity.getThumbnailURL());
                    myDemucsSongDTO.setSongTime(songEntity.getSongTime());
                    myDemucsSongDTO.setMessageId(songEntity.getMessageId());
                    myDemucsSongDTO.setDemucsCompleted(songEntity.isDemucsCompleted());
                    return myDemucsSongDTO;
                }
        ).toList();
    }

    public List<Long> assignSongToQuizinDB(List<Long> songIds, long quizId){
        QuizEntity quizEntity = quizRepository.findById(quizId).orElse(null);
        if(quizEntity == null){
            return null;
        }
        List<Long> qsRelationIdList = new ArrayList<>();
        for (long songID : songIds) {
            SongEntity songEntity = songRepository.findById(songID).orElse(null);
            System.out.println("노래명 : " + songEntity.getSongName());
            if(quizSongRelationRepository.findByQuizEntity_QuizIdAndSongEntity_SongId(quizId, songEntity.getSongId()) == null) { //퀴즈에 없는 노래만 추가
                System.out.println("이후 이프문 통과중");
                QuizSongRelation quizSongRelation = new QuizSongRelation();
                quizSongRelation.setQuizEntity(quizEntity);
                quizSongRelation.setSongEntity(songEntity);
                quizSongRelation = quizSongRelationRepository.save(quizSongRelation);
                qsRelationIdList.add(quizSongRelation.getQSRelationId());
            }

        }
        quizEntity.setSongCount(quizEntity.getSongCount() + qsRelationIdList.size());
        quizRepository.save(quizEntity);
        return qsRelationIdList;
    }

    public DemucsConvertCountDTO getUserDemucsCount(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        UserEntity userEntity = userRepository.findByEmail(email);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime last24Hours = now.minusHours(24);


        List<SongEntity> allSongs = songRepository.findSongsByUserAndDate(userEntity, last24Hours);

        System.out.println("Count : " + allSongs.size());

        List<SongEntity> notCompletedSongs = allSongs.stream()
                .filter(s -> !s.isDemucsCompleted())
                .toList();

        List<SongEntity> completedSongs = allSongs.stream()
                .filter(SongEntity::isDemucsCompleted)
                .toList();

        DemucsConvertCountDTO dto = new DemucsConvertCountDTO();
        dto.setOrderCount(notCompletedSongs.size() + completedSongs.size());
        dto.setConvertedCount(completedSongs.size());

        return dto;
    }


    // 다운로드 링크에서 파일을 다운로드하여 resources에 저장
    private void downloadFile(String fileUrl, String destinationPath) throws IOException {
        URL url = new URL(fileUrl);
        // 경로가 없다면 폴더 생성

        System.out.println("데스티네이션 패스 : " + destinationPath);
        File destinationFile = new File(destinationPath);
        File parentDir = destinationFile.getParentFile();
        if (!parentDir.exists()) {
            // 부모 디렉토리 경로가 존재하지 않으면 디렉토리 생성
            parentDir.mkdirs();
            System.out.println("Directory created: " + parentDir.getAbsolutePath());
        }
        try (BufferedInputStream in = new BufferedInputStream(url.openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(destinationPath)) {
            byte[] dataBuffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
            System.out.println("File downloaded to: " + destinationPath);
        }
    }

    public DemucsQuizConvertedListDTO getQuizDemucsCountInDB(long quiz){
        List<QuizSongRelation> quizSongRelationList = quizSongRelationRepository.findByQuizEntity_QuizId(quiz);

        System.out.println("QS갯수 : " + quizSongRelationList.size());

        DemucsQuizConvertedListDTO dto = new DemucsQuizConvertedListDTO();
        for (QuizSongRelation quizSongRelation : quizSongRelationList) {
            if(!quizSongRelation.getSongEntity().isDemucsCompleted()){ // 포함된게 변환안됬으면
                dto.notConvertedQsRelationIDInQuizList.add(quizSongRelation.getQSRelationId());
            }
            dto.AllQsRelationIDInQuizList.add(quizSongRelation.getQSRelationId());
        }
        return dto;
    }

    // 메시지에서 download link 추출 후 다운로드
    private void downloadFilesFromMessage(String payload) {

        try{
            // JSON을 객체로 변환
            ObjectMapper objectMapper = new ObjectMapper();
            MessageDTO messageData = objectMapper.readValue(payload, MessageDTO.class);

            // 다운로드 링크 추출
            Map<String, String> downloadLinks = messageData.getDownload_links();
            String songId = messageData.getSong_id();


            SongEntity songEntity = songRepository.findById(Long.parseLong(songId)).orElse(null);
            if(songEntity != null){


                // 다운로드 및 저장
                for (Map.Entry<String, String> entry : downloadLinks.entrySet()) {
                    String linkType = entry.getKey();  // 예: no_vocals, vocals, drums, bass
                    String fileUrl = baseDownloadUrl +encodeUrl(entry.getValue()); // 실제 파일 URL

                    System.out.println("Download type: " + linkType + " | URL: " + fileUrl);

                    String destinationPath = demucsLocalDir + songId + "_" + linkType + ".mp3";
                    // 파일 다운로드 함수 호출
                    downloadFile(fileUrl, destinationPath);
                    songEntity.setDemucsCompleted(true);
                    songRepository.save(songEntity);
                }
            }



        }catch (Exception e){
            e.printStackTrace();
        }

    }

    // URL 인코딩 처리 (띄어쓰기도 처리)
    private String encodeUrl(String url) throws UnsupportedEncodingException {
        // 모든 문자를 인코딩하지만, 슬래시('/')는 인코딩하지 않음
        String encodedUrl = URLEncoder.encode(url, StandardCharsets.UTF_8.toString());
        return encodedUrl.replace("+", "%20").replace("%2F", "/"); // +을 %20으로, %2F는 /로 되돌리기
    }

    private DemucsSongDTO getYoutubeSong(String youtubeURL) {
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
            return mapToYoutubeSong(itemNode);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String extractYoutubeVideoId(String youtubeURL) {
        String regex = "(?<=v=|/|be/)([a-zA-Z0-9_-]{11})";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(youtubeURL);
        return matcher.find() ? matcher.group(0) : null;
    }

    private DemucsSongDTO mapToYoutubeSong(JsonNode itemNode) {
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
                songEntity.setDemucsCompleted(false);
                songEntity = songRepository.save(songEntity);
            }

            if(existSinger == null || existPlatformSong == null){
                SingerSongRelation singerSongRelation = new SingerSongRelation();
                singerSongRelation.setSong(songEntity);
                singerSongRelation.setSinger(singerEntity);
                singerSongRelationRepository.save(singerSongRelation);
            }

            return getDemucsSongDTO(songEntity, singerEntity);

        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static DemucsSongDTO getDemucsSongDTO(SongEntity songEntity, SingerEntity singerEntity) {
        DemucsSongDTO demucsSongDTO = new DemucsSongDTO();
        demucsSongDTO.setSongId(songEntity.getSongId());
        demucsSongDTO.setSongPlatformId(songEntity.getSongPlatformId());
        demucsSongDTO.setSongName(songEntity.getSongName());
        demucsSongDTO.setSingerName(singerEntity.getSingerName());
        demucsSongDTO.setPlayURL(songEntity.getPlayURL());
        demucsSongDTO.setThumbnailURL(songEntity.getThumbnailURL());
        demucsSongDTO.setSongTime(songEntity.getSongTime());

        return demucsSongDTO;
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
}
