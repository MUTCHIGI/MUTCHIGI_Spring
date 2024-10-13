package com.CAUCSD.MUTCHIGI.quiz.hint;

import com.CAUCSD.MUTCHIGI.quiz.QuizEntity;
import com.CAUCSD.MUTCHIGI.song.SongEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
@Entity
public class HintEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long hintId;

    @ManyToOne
    @JoinColumn(name = "quizID", referencedColumnName = "quizId")
    private QuizEntity quizEntity;

    @ManyToOne
    @JoinColumn(name = "songID", referencedColumnName = "songId")
    private SongEntity songEntity;

    private LocalTime hintTime;

    private String hintType;

    @Column(columnDefinition = "TEXT")
    private String hintText;
}
