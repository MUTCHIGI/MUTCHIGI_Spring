package com.CAUCSD.MUTCHIGI.song.demucs;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/GCP")
public class GCPPubSubController {

    @Autowired
    private GCPPubSubService gcpPubSubService;

    @PostMapping("/publish")
    @Operation(summary = "악기분리 요청 API, 유튜브URL만 넣으면 됨")
    public DemucsSongDTO publishMessage(
            @RequestParam String youtubeURL
    ){
        return gcpPubSubService.publicMessage(youtubeURL);
    }

    @GetMapping("/DemucsSong/play")
    @Operation(summary = "악기분리된 노래를 듣는 Inline을 보내는 API, 스웨거에서는 실행 안됨")
    public ResponseEntity<InputStreamResource> playDemucsSongFile(
            @RequestParam long songId,
            @RequestParam int instrumentId
    ) throws IOException {

        FileInputStream fileInputStream = gcpPubSubService.playDemucsSong(songId, instrumentId);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION,  "inline; filename=demucs_" + songId + "_" + instrumentId + ".mp3");

        InputStreamResource resource = new InputStreamResource(fileInputStream);
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.valueOf("audio/mpeg"))
                .contentLength(fileInputStream.getChannel().size())
                .body(resource);
    }

    @GetMapping("/DemucsSong/play/inRoom")
    @Operation(summary = "STOMP에서 악기분리된 노래를 듣는 Inline을 보내는 API, 스웨거에서는 실행 안됨")
    public ResponseEntity<InputStreamResource> playDemucsSongFileInRoom(
            @RequestParam long qsRelationId
    ) throws IOException {

        FileInputStream fileInputStream = gcpPubSubService.playDemucsSongInRoom(qsRelationId);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION,  "inline; filename=demucs_" + qsRelationId +  ".mp3");

        InputStreamResource resource = new InputStreamResource(fileInputStream);
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.valueOf("audio/mpeg"))
                .contentLength(fileInputStream.getChannel().size())
                .body(resource);
    }

    @GetMapping("/DemucsSong/List")
    @Operation(summary = "기존 변환된 노래들 offset에 따라 정해서 가져오는 API")
    public ResponseEntity<List<DemucsSongDTO>> listDemucsSong(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "12") int offset,
            @RequestParam(defaultValue = "") String songTitle
    ){
        List<DemucsSongDTO> getList = gcpPubSubService.getListDemucsSong(page-1, offset, songTitle);
        return ResponseEntity.ok(getList);
    }

    @GetMapping("/DemucsSong/myOrderList")
    @Operation(summary = "내가 변환한 노래들 중 아직 변환되지 않은 것들 가져오는 API")
    public ResponseEntity<List<MyDemucsSongDTO>> myOrderList(){

        List<MyDemucsSongDTO> getList = gcpPubSubService.getMyDemucsList();
        return ResponseEntity.ok(getList);
    }

    @PostMapping("/DemucsSong/SongToQuiz")
    @Operation(summary = "노래Id들을 퀴즈에 할당하고 QuizSongRelationId 리스트를 얻는 API")
    public ResponseEntity<List<Long>> assignSongToQuiz(
            @RequestParam List<Long> songIds,
            @RequestParam long quizId
    ){
        return ResponseEntity.ok()
                .body(gcpPubSubService.assignSongToQuizinDB(songIds, quizId));
    }
}
