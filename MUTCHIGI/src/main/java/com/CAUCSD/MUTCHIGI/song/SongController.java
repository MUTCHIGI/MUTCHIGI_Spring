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
    @Operation(summary = "해당 퀴즈의 힌트 개수를 확인할 수 있는 API",
    description = """
            해당 퀴즈가 총 몇개의 힌트를 주도록 정해두었는 지 확인 할 수 있는 API이다.
            """)
    public ResponseEntity<Integer> getHintCount(
            @RequestParam long quizId
    ){
        return ResponseEntity
                .ok()
                .body(songService.getHintCountFromDB(quizId));
    }

    @PostMapping("/youtube/{qsRelationId}/hint")
    @Operation(summary = "해당 퀴즈에 포함된 노래에 대한 퀴즈를 저장하는 API, 갯수는 위에서 확인함",
            description = """
                               /youtube/hintCount에서 얻은 퀴즈 갯수로 Front에서 예외처리를 일단 해야 한다.
                               백엔드에서는 아직 구현되어 있지 않음. 구현되면 추가하겠음.
                                """)
    public ResponseEntity<List<Long>> addHintList(
            @RequestBody List<HintDTO> hintDTOList,
            @PathVariable long qsRelationId
    ){
        return ResponseEntity
                .ok()
                .body(songService.saveHintList(hintDTOList, qsRelationId));
    }

    @DeleteMapping("/youtube/{qsRelationId}/delSong")
    @Operation(summary = "해당 퀴즈에 대한 노래(유튜브 영상)을 삭제하는 API", description =
            """
                    퀴즈-노래 사이의 관련성(qsRelation)만 삭제한다. 즉, 노래 정보나 가수 정보는 그대로 DB에 남아있어서 재활용이 가능하다.
                    실제로 qsRelation만 삭제하고 같은 노래 URL을 넣으면 동일하기에 노래 DB에 있던 id를 그대로 반환한다.
                    """)
    public ResponseEntity<Void> deleteYoutubeSong(
            @PathVariable long qsRelationId
    ){
        return ResponseEntity
                .ok()
                .body(songService.deleteYoutubeSongDB(qsRelationId));
    }
}
