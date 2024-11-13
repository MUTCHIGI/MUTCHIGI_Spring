package com.CAUCSD.MUTCHIGI.quizSong.hint;

import lombok.Data;

import java.time.LocalTime;

@Data
public class GetHintDTO {

    private long hintId;
    private LocalTime hintTime;
    private String hintType;
    private String hintText;
}
