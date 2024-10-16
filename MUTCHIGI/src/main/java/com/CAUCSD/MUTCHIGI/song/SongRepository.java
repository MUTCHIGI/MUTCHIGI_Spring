package com.CAUCSD.MUTCHIGI.song;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SongRepository extends JpaRepository<SongEntity, Long> {
    SongEntity findBySongPlatformId(String songPlatformId);
}
