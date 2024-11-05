package com.CAUCSD.MUTCHIGI.quizSong.hint;

import com.CAUCSD.MUTCHIGI.quiz.QuizEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HintStateRepository extends JpaRepository<HintStateEntity, Long> {
    List<HintStateEntity> findByQuizEntity(QuizEntity quizEntity);
}
