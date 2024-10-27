package com.CAUCSD.MUTCHIGI.quiz;

import io.swagger.v3.oas.annotations.Operation;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
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
        List<QuizEntity> quizEntities;
        try {
            quizEntities = quizRepository.findAllById(idList);
            if(quizEntities.isEmpty()){
                return ResponseEntity.status(HttpStatus.SC_NOT_FOUND).build();
            }
            return ResponseEntity.ok(quizEntities);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).build();
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
            return ResponseEntity.status(HttpStatus.SC_NOT_IMPLEMENTED).build();
        }
        return ResponseEntity.status(HttpStatus.SC_CREATED).body(createdQuiz.getQuizId());
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
}
