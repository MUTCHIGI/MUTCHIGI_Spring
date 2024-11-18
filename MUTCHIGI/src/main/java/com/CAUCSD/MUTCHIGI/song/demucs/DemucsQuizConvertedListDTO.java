package com.CAUCSD.MUTCHIGI.song.demucs;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DemucsQuizConvertedListDTO {
    List<Long> notConvertedQsRelationIDInQuizList;
    List<Long> AllQsRelationIDInQuizList;

    public DemucsQuizConvertedListDTO() {
        this.AllQsRelationIDInQuizList = new ArrayList<>();
        this.notConvertedQsRelationIDInQuizList = new ArrayList<>();
    }
}
