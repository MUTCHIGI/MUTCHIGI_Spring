package com.CAUCSD.MUTCHIGI.room.Member;

import com.CAUCSD.MUTCHIGI.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<MemberEntity, Long> {
    List<MemberEntity> findByRoomEntity_RoomId(long roomId);
    Optional<MemberEntity> findByRoomEntity_RoomIdAndUserEntity_UserId(long roomId, long userId);
    MemberEntity findByUserEntity(UserEntity userEntity);
}
