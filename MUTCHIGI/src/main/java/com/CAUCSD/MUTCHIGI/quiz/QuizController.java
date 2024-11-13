package com.CAUCSD.MUTCHIGI.quiz;

import com.CAUCSD.MUTCHIGI.quizSong.hint.GetHintStateDTO;
import com.CAUCSD.MUTCHIGI.quizSong.hint.HintStateDTO;
import com.CAUCSD.MUTCHIGI.quizSong.hint.HintStateEntity;
import io.swagger.v3.oas.annotations.Operation;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/quiz")
public class QuizController {

    @Autowired
    private QuizService quizService;
    @Autowired
    private QuizRepository quizRepository;

    @GetMapping("/idList")
    @Operation(summary = "/home 계층 ID 리스트만 요청", description = """
      퀴즈 ID List만 제공하는 API임.
      page 기본 : 1 (1부터 시작함, 0 시작 X)
      offset 기본 : 8 (페이지당 8 보여줌)
      정렬 기본 : 날짜 최신순(DATEDS)
            DATEAS(1, "dateAscending"),
            DATEDS(2, "dateDescending"),
            NAMEAS(3, "nameAscending"),
            NAMEDS(4, "nameDescending"),
            VIEWAS(5, "viewAscending"),
            VIEWDS(6, "viewDescending");
      quizTitle 기본 : ""(빈 String => 전체 조회가능)
      modId 기본 : 0 (0 : 전체, 1 : 커스텀, 2 : 플레이리스트)
      typeId 기본 : 0 (0 : 전체, 1 : 기본, 2 : 악기분리)
    """)
    public ResponseEntity<List<Long>> getPageIDList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "8") int offset,
            @RequestParam(defaultValue = "DATEDS") QuizSort sort,
            @RequestParam(defaultValue = "") String quizTitle,
            @RequestParam(defaultValue = "0") int modId,
            @RequestParam(defaultValue = "0") int typeId
    ){
        List<Long> quizIDList = quizService.getQuizIDList(page-1, offset, typeId, modId, sort, quizTitle);

        if (quizIDList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.SC_NO_CONTENT).build();
        }

        return ResponseEntity.ok(quizIDList);
    }

    @GetMapping("/Entities")
    @Operation(summary = "전에 받은 idList를 RequsetParam에 담아서 보내주면 됩니다.")
    public  ResponseEntity<List<QuizEntity>> getQuizEntities(
            @RequestParam List<Long> idList
    ){
       try {
            return ResponseEntity.ok(quizService.getQuizByIdList(idList));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/images/{filename}")
    @Operation(summary = "퀴즈 썸네일 이미지를 inline으로 반환함",
    description = """
            퀴즈 썸네일 이미지 filename은 QuizEnitiy로 반환받은 것중 thumbnailURL에 해당함.
            이거를 CONTENT_DISPOSITION과 inline 형태로 반환할 예정임.
            이미지를 PNG형식 고정
            """)
    public ResponseEntity<Resource> serveImage(@PathVariable String filename) throws Exception {

        Resource imageResource = quizService.serveImageFromLocalStorage(filename);
        if (imageResource.exists() || imageResource.isReadable()) {
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + imageResource.getFilename() + "\"")
                    .contentType(MediaType.IMAGE_PNG)
                    .body(imageResource);
        } else {
            throw new RuntimeException("Could not read the file: " + filename);
        }
    }
    
    @PostMapping(value = "/createQuiz")
    @Operation(summary = "quiz 만들기", description = """
            modId : 1(커스텀), 2(플레이리스트)
            typeId : 1(기본), 2(악기 분리)
            instrumentId : 0(typeId가 1이면), 1(보컬), 2(베이스), 3(반주), 4(드럼)
            """)
    public ResponseEntity<Long> createQuiz(
            @RequestBody QuizDTO quizDTO
            ){

        QuizEntity createdQuiz = quizService.createQuiz(quizDTO);

        if (createdQuiz == null) {
            return ResponseEntity.status(HttpStatus.SC_NOT_IMPLEMENTED).build(); //501
        }else if(createdQuiz.getQuizId() == -1){
            return ResponseEntity.status(HttpStatus.SC_METHOD_NOT_ALLOWED).build(); // 405
        }
        return ResponseEntity.status(HttpStatus.SC_CREATED).body(createdQuiz.getQuizId());
    }

    @PostMapping(value = "/setReady/{quizId}")
    @Operation(summary = "여기서 최종적으로 Ready된 Quiz만 조회가능하도록 로직이 변경됨")
    public ResponseEntity<Long> setQuizToReady(
            @PathVariable long quizId
    ){

        return ResponseEntity.status(HttpStatus.SC_OK).body(quizService.setQuizToReadyInDB(quizId));
    }

    @PostMapping(value = "/createQuiz/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "quiz 업로드(image 제외)")
    public ResponseEntity<Long> createQuizImage(
            @RequestParam("image") MultipartFile image,
            @RequestParam("quizId") long quizId
    ){
        QuizEntity quizEntity = quizService.getQuizById(quizId);

        if(quizEntity == null){
            return ResponseEntity.status(HttpStatus.SC_NOT_FOUND).build();
        }

        try{
            System.out.println("여기도 테스트");
            String imagePath = quizService.saveThumbnailURL(image, quizId);
            System.out.println("여기 진입되는 지 테스트" + imagePath);
            quizEntity.setThumbnailURL(imagePath);
            quizEntity= quizService.updateQuiz(quizEntity);

            return ResponseEntity.status(HttpStatus.SC_OK).body(quizEntity.getQuizId());
        }catch(IOException e){
            return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{quizId}/hintState")
    @Operation(summary = "퀴즈 저장직후 hint갯수와 상태 관리함")
    public ResponseEntity<List<Long>> setYoutbueHintState(
            @PathVariable long quizId,
            @RequestBody List<HintStateDTO> hintStateDTOList

    ){
        return ResponseEntity
                .ok()
                .body(quizService.setYoutbueHintStateToDB(hintStateDTOList, quizId));
    }

    @GetMapping("/{quizId}/hintState")
    @Operation(summary = "퀴즈 저장직후 hint 상태 가져오기")
    public ResponseEntity<List<GetHintStateDTO>> getYoutbueHintStateFromDB(
            @PathVariable long quizId
    ){
        return ResponseEntity.ok(quizService.getHintStateByHintId(quizId));
    }

    @GetMapping("/notReadyQuizList")
    @Operation(summary = "아직 업로드 되지 않은 자신의 Quiz의 간단한 정보를 얻는 API")
    public ResponseEntity<List<NotReadyQuizDTO>> getNotReadyQuizList(){
        return ResponseEntity.ok(quizService.getNotReadyQuiz());
    }

    @DeleteMapping("/deleteNotReadyQuiz/{quizId}")
    @Operation(summary = "quizID로 준비되지 않은 퀴즈 삭제")
    public ResponseEntity<Void> deleteNotReadyQuiz(
            @PathVariable long quizId
    ){
        quizService.deleteNotReadyQuizInDB(quizId);
        return ResponseEntity.ok().build();
    }


}
