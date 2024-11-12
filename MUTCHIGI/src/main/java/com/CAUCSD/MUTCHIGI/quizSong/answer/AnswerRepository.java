package com.CAUCSD.MUTCHIGI.quizSong.answer;

import com.CAUCSD.MUTCHIGI.quizSong.QuizSongRelation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnswerRepository extends JpaRepository<AnswerEntity,Long> {
    List<AnswerEntity> findByQuizSongRelation(QuizSongRelation quizSongRelation);
}
