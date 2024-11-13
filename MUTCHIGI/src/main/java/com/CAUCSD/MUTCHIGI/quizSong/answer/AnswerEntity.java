package com.CAUCSD.MUTCHIGI.quizSong.answer;

import com.CAUCSD.MUTCHIGI.quizSong.QuizSongRelation;
import com.CAUCSD.MUTCHIGI.song.SongEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class AnswerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long answerId;

    @ManyToOne
    @JoinColumn(name = "qsSongRealtionId", referencedColumnName = "QSRelationId")
    private QuizSongRelation quizSongRelation;

    private String answer;

    private boolean LLMUsed;
}
