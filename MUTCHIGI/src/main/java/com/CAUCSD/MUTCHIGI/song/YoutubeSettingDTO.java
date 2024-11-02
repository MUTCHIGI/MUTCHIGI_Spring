package com.CAUCSD.MUTCHIGI.song;

import com.CAUCSD.MUTCHIGI.quizSong.hint.HintDTO;
import lombok.Data;

import java.util.List;

@Data
public class YoutubeSettingDTO {

    private TimeDTO startTime;
    private List<String> answerList;
    private List<HintDTO> hintDTOList;
}
