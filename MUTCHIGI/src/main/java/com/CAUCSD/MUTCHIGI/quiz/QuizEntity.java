package com.CAUCSD.MUTCHIGI.quiz;

import com.CAUCSD.MUTCHIGI.user.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@Entity
public class QuizEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long quizId;

    private int songCount;
    // 퀴즈의 노래 갯수

    @Column(unique=true)
    private String quizName;

    @Column(columnDefinition = "TEXT")
    private String quizDescription;
    // 퀴즈 설명

    private LocalDateTime releaseDate;
    // 최종 수정 일자

    private long userPlayCount;
    // 몇 명의 유저가 플레이 했는가?

    private int typeId;
    // 기본, 악기분리

    private int modId;
    // 커스텀, 나만의 플레이리스트 등등

    private int hintCount;
    // 힌트 제한 갯수

    private LocalTime songPlayTime;
    // 노래 제한시간

    private boolean UseDisAlg;
    // 편집 알고리즘 사용유무

    private String thumbnailURL;
    //퀴즈 썸네일 URL임.

    private int instrumentId;
    // Instrument Enum Id 저장

    private boolean readyToPlay;

    @ManyToOne
    @JoinColumn(name = "quizAuthorId", referencedColumnName = "userId")
    private UserEntity user;
    // 제작자 userId

}
