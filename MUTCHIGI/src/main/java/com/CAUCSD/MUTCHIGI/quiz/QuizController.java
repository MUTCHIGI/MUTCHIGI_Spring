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
    @Operation(summary = "/home 계층 ID 리스트만 요청", description = "퀴즈 ID List만 제공하는 API입니다. 기본 분류는 날짜 내림차순입니다.")
    public ResponseEntity<List<Long>> getPageIDList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "8") int offset,
            @RequestParam(defaultValue = "DATEDS") QuizSort sort
    ){
        List<Long> quizIDList = quizService.getQuizIDList(page-1, offset, sort);

        if (quizIDList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.SC_NO_CONTENT).build();
        }

        return ResponseEntity.ok(quizIDList);
    }

    @GetMapping("/Entity")
    public  ResponseEntity<QuizEntity> getQuizList(
            @RequestParam long quizId
    ){
        try {
            QuizEntity quizEntity = quizRepository.findById(quizId).orElse(null);
            if(quizEntity == null){
                return ResponseEntity.status(HttpStatus.SC_NOT_FOUND).build();
            }
            return ResponseEntity.ok(quizEntity);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping(value = "/createQuiz")
    @Operation(summary = "quiz 만들기")
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
