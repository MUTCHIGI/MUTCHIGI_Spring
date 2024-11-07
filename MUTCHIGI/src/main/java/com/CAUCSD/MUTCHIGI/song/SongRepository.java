package com.CAUCSD.MUTCHIGI.song;

import com.CAUCSD.MUTCHIGI.user.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SongRepository extends JpaRepository<SongEntity, Long> {
    SongEntity findBySongPlatformId(String songPlatformId);
    Page<SongEntity> findBySongNameContainingAAndDemucsCompletedTrue(String songName, Pageable pageable);
    List<SongEntity> findSongEntitiesByUser(UserEntity user);
}
