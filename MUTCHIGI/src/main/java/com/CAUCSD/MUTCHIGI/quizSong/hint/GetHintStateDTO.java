package com.CAUCSD.MUTCHIGI.quizSong.hint;

import lombok.Data;

import java.time.LocalTime;

@Data
public class GetHintStateDTO {

    private long hintStateId;
    private LocalTime hintTime;
    private String hintType;
}
