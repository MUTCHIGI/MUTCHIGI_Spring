package com.CAUCSD.MUTCHIGI.quizSong.hint;

import lombok.Data;

@Data
public class HintAllDTO {

    private int hour;
    private int minute;
    private int second;
    private String hintType;
    private String hintText;
}
