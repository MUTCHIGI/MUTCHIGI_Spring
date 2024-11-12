package com.CAUCSD.MUTCHIGI.song;

import com.CAUCSD.MUTCHIGI.user.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface SongRepository extends JpaRepository<SongEntity, Long> {
    SongEntity findBySongPlatformId(String songPlatformId);
    Page<SongEntity> findBySongNameContainingAndDemucsCompletedIsTrue(String songName, Pageable pageable);
    List<SongEntity> findSongEntitiesByUserAndDemucsCompletedIsFalse(UserEntity user);

    @Query("SELECT s FROM SongEntity s WHERE s.user = :user AND s.convertOrderDate >= :startDate")
    List<SongEntity> findSongsByUserAndDate(@Param("user") UserEntity user, @Param("startDate") LocalDateTime startDate);
}
