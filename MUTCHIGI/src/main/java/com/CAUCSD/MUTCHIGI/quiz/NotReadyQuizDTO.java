package com.CAUCSD.MUTCHIGI.quiz;

import lombok.Data;

@Data
public class NotReadyQuizDTO {

    private long quizId;
    private String quizName;
    private String imageFileName;
    private int songCount;
}
