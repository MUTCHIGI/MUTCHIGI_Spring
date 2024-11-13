package com.CAUCSD.MUTCHIGI.quizSong.hint;

import com.CAUCSD.MUTCHIGI.quiz.QuizEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
@Entity
public class HintStateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long hintStateId;

    @ManyToOne
    @JoinColumn(name = "quizId", referencedColumnName = "quizId")
    private QuizEntity quizEntity;

    private LocalTime hintTime;

    private String hintType;

}
