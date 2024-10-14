package com.CAUCSD.MUTCHIGI.song.singer;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SingerRepository extends JpaRepository<SingerEntity, Long> {
    SingerEntity findBySingerName(String singerName);
}
