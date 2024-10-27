package com.CAUCSD.MUTCHIGI.song;

import com.CAUCSD.MUTCHIGI.quiz.QuizRepository;
import com.CAUCSD.MUTCHIGI.quiz.hint.HintDTO;
import com.CAUCSD.MUTCHIGI.quizSong.QuizSongRelationReopository;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/song")
public class SongController {

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private SongService songService;

    @Autowired
    private QuizRepository quizRepository;

    @PostMapping("/youtube")
    @Operation(summary = "퀴즈에 유튜브 영상을 추가하는 API", description = " 유튜브 URL과 추가할 QuizId를 담아 전송하면 DB에 저장된 Youtube정보 나옴")
    public ResponseEntity<YoutubeSongDTO> createYoutubeToQuiz(
            @RequestBody YoutubeSongRequsetDTO youtubeSongRequsetDTO
    ){

        return ResponseEntity
                .ok()
                .body(songService.getYoutubeSong(
                        youtubeSongRequsetDTO.getYoutubeURL(),
                        youtubeSongRequsetDTO.getQuizId())
                );
    }

    @PostMapping("/youtube/{qsRelationId}/settings")
    @Operation(summary = "유튜브 영상 퀴즈에 대한 정답, 시작시간, 힌트를 한꺼번에 세팅하는 API")
    public ResponseEntity<Void> setYoutubeSetting(
            @RequestBody YoutubeSettingDTO youtubeSettingDTO,
            @PathVariable long qsRelationId
    ){
        songService.saveStartTime(youtubeSettingDTO.getStartTime(), qsRelationId);
        songService.saveYoutubeAnswer(youtubeSettingDTO.getAnswerList(), qsRelationId);
        songService.saveHintList(youtubeSettingDTO.getHintDTOList(), qsRelationId);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/youtube/{qsRelationId}/startTime")
    @Operation(summary = "특정 퀴즈에서 특정 노래 시작 시간 설정하는 API", description = "/youtube 도메인에서 제공된 QSRelationId를 PathVariable로 담아 전송")
    public ResponseEntity<LocalTime> addStartTime(
            @RequestBody TimeDTO startTime,
            @PathVariable long qsRelationId
    ){
        return ResponseEntity
                .ok()
                .body(songService.saveStartTime(startTime, qsRelationId));
    }

    @PostMapping("/youtube/{qsRelationId}/answers")
    @Operation(summary = "특정 퀴즈의 특정 노래에 대한 정답 리스트를 전송하는 API", description = "/youtube 도메인에서 제공된 QSRelationId를 PathVariable로 담아 전송")
    public ResponseEntity<List<Long>> addYoutbueAnswer(
            @RequestBody List<String> answerList,
            @PathVariable long qsRelationId
    ){
        return ResponseEntity
                .ok()
                .body(songService.saveYoutubeAnswer(answerList, qsRelationId));
    }

    @GetMapping("/youtube/hintCount")
    public ResponseEntity<Integer> getHintCount(
            @RequestParam long quizId
    ){
        return ResponseEntity
                .ok()
                .body(songService.getHintCountFromDB(quizId));
    }

    @PostMapping("/youtube/{qsRelationId}/hint")
    public ResponseEntity<List<Long>> addHintList(
            @RequestBody List<HintDTO> hintDTOList,
            @PathVariable long qsRelationId
    ){
        return ResponseEntity
                .ok()
                .body(songService.saveHintList(hintDTOList, qsRelationId));
    }

    @PostMapping("youtube/myPlaylist")
    public ResponseEntity<List<YoutubeSongDTO>> addMyPlaylistToQuiz(
            @RequestBody MyPlayListQuizDTO myPlayListQuizDTO
    ){
        return ResponseEntity
                .ok()
                .body(songService.addMyPlayListToQuiz(myPlayListQuizDTO));

    }
}
