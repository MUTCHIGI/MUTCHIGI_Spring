package com.CAUCSD.MUTCHIGI.quizSong.hint;

import com.CAUCSD.MUTCHIGI.quiz.QuizEntity;
import com.CAUCSD.MUTCHIGI.quizSong.QuizSongRelation;
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
    @JoinColumn(name = "qsSongRealtionId", referencedColumnName = "QSRelationId")
    private QuizSongRelation quizSongRelation;

    private LocalTime hintTime;

    private String hintType;

    @Column(columnDefinition = "TEXT")
    private String hintText;
}
