package com.CAUCSD.MUTCHIGI.song;

import com.CAUCSD.MUTCHIGI.user.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Setter
@Getter
@Entity
public class SongEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long songId;

    private String songPlatformId;

    private String songName;

    private String playURL;

    private LocalTime songTime;

    private String thumbnailURL;

    private boolean demucsCompleted;

    private LocalDateTime convertOrderDate;

    private String messageId;

    @ManyToOne
    @JoinColumn(name = "demucsOrderUserId", referencedColumnName = "userId")
    private UserEntity user;
}
