package com.CAUCSD.MUTCHIGI.room;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<RoomEntity, Long> {
    // modId와 typeId가 모두 0인 경우
    Page<RoomEntity> findByQuiz_QuizNameContainingAndPublicRoom(String quizTitle, boolean publicRoom, Pageable pageable);

    // modId가 0이고 typeId가 있는 경우
    Page<RoomEntity> findByQuiz_QuizNameContainingAndQuiz_TypeIdAndPublicRoom(String quizTitle, int typeId, boolean publicRoom, Pageable pageable);

    // typeId가 0이고 modId가 있는 경우
    Page<RoomEntity> findByQuiz_QuizNameContainingAndQuiz_ModIdAndPublicRoom(String quizTitle, int modId, boolean publicRoom, Pageable pageable);

    // modId와 typeId가 모두 있는 경우
    Page<RoomEntity> findByQuiz_QuizNameContainingAndQuiz_ModIdAndQuiz_TypeIdAndPublicRoom(String quizTitle, int modId, int typeId, boolean publicRoom, Pageable pageable);

}
