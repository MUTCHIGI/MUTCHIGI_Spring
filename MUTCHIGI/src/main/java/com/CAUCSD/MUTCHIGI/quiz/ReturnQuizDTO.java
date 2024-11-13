package com.CAUCSD.MUTCHIGI.quiz;

import lombok.Data;
import org.springframework.core.io.FileSystemResource;

@Data
public class ReturnQuizDTO {

    private QuizEntity quizEntity;
    private String thumnail;
}
