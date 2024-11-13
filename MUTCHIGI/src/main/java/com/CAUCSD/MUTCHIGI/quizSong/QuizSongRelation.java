package com.CAUCSD.MUTCHIGI.quizSong;

import com.CAUCSD.MUTCHIGI.quiz.QuizEntity;
import com.CAUCSD.MUTCHIGI.song.SongEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
@Entity
public class QuizSongRelation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long QSRelationId;
    //기본 연결 키 필드

    @ManyToOne
    @JoinColumn(name = "quizId", referencedColumnName = "quizId")
    private QuizEntity quizEntity;

    @ManyToOne
    @JoinColumn(name = "songId", referencedColumnName = "songId")
    private SongEntity songEntity;

    private LocalTime startTime;
}
