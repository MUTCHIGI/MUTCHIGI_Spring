package com.CAUCSD.MUTCHIGI.room;

import com.CAUCSD.MUTCHIGI.quiz.QuizEntity;
import com.CAUCSD.MUTCHIGI.user.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
public class RoomEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long roomId;

    private String roomName;

    private boolean publicRoom;

    private String password;

    private int maxPlayer;

    private boolean participateAllowed;

    private LocalDate roomReleaseDate;

    @ManyToOne
    @JoinColumn(name = "quizReferencingId", referencedColumnName = "quizId")
    private QuizEntity quiz;
}
