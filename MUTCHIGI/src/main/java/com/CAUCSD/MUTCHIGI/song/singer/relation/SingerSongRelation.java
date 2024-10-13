package com.CAUCSD.MUTCHIGI.song.singer.relation;

import com.CAUCSD.MUTCHIGI.song.SongEntity;
import com.CAUCSD.MUTCHIGI.song.singer.SingerEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class SingerSongRelation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long relationID;

    @ManyToOne
    @JoinColumn(name = "singerID", referencedColumnName = "singerId")
    private SingerEntity singer;

    @ManyToOne
    @JoinColumn(name = "songID", referencedColumnName = "songId")
    private SongEntity song;

}
