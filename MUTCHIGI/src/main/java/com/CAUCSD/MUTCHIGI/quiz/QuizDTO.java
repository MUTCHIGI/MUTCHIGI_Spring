package com.CAUCSD.MUTCHIGI.quiz;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "퀴즈 데이터 전송 객체")
public class QuizDTO {
    @Schema(description = "퀴즈 이름")
    private String quizName;

    @Schema(description = "퀴즈 설명")
    private String quizDescription;

    @Schema(description = "퀴즈 타입 ID")
    private int typeId;

    @Schema(description = "모드 ID")
    private int modId;

    @Schema(description = "퀴즈 플레이 시간 (시간)")
    private int hour;

    @Schema(description = "퀴즈 플레이 시간 (분)")
    private int minute;

    @Schema(description = "퀴즈 플레이 시간 (초)")
    private int second;
    @Schema(description = "악기 ID")
    private int instrumentId;

    @Schema(description = "사용자 ID")
    private long userId;

    @Schema(description = "알고리즘 사용 여부")
    private boolean useDisAlg;

}
