package com.CAUCSD.MUTCHIGI.song.singer.relation;

import com.CAUCSD.MUTCHIGI.song.SongEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SingerSongRelationRepository extends JpaRepository<SingerSongRelation,Long> {
    SingerSongRelation findBySong(SongEntity song);

}
