package com.CAUCSD.MUTCHIGI.song.answer;

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
    @JoinColumn(name = "songID", referencedColumnName = "songId")
    private SongEntity song;

    private String answer;

    private boolean isLLMUsed;
}
