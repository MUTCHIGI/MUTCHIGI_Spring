package com.CAUCSD.MUTCHIGI.quizSong.hint;

import lombok.Data;

import java.time.LocalTime;

@Data
public class HintStateDTO {
    private int hour;
    private int minute;
    private int second;
    private String hintType;
}
