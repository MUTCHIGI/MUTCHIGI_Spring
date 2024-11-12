package com.CAUCSD.MUTCHIGI.song.demucs;

import com.CAUCSD.MUTCHIGI.song.SongEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class DemucsSongEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long demucsSongId;

    @ManyToOne
    @JoinColumn(name = "songID", referencedColumnName = "songId")
    private SongEntity song;

    private int instrumentID;

    private String directoryURL;
}
