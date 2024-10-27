package com.CAUCSD.MUTCHIGI.quiz;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface QuizRepository extends JpaRepository<QuizEntity, Long> {
    // 모든 조건이 포함된 메소드
    Page<QuizEntity> findByQuizNameContainingAndTypeIdAndModId(
            String quizTitle, Integer typeId, Integer modId, Pageable pageable);

    // modId가 null인 경우
    Page<QuizEntity> findByQuizNameContainingAndTypeId(
            String quizTitle, Integer typeId, Pageable pageable);

    // typeId가 null인 경우
    Page<QuizEntity> findByQuizNameContainingAndModId(
            String quizTitle, Integer modId, Pageable pageable);

    // typeId와 modId가 모두 null인 경우
    Page<QuizEntity> findByQuizNameContaining(
            String quizTitle, Pageable pageable);
}
