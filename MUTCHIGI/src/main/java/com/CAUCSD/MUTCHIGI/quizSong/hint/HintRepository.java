package com.CAUCSD.MUTCHIGI.quizSong.hint;

import com.CAUCSD.MUTCHIGI.quizSong.QuizSongRelation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HintRepository extends JpaRepository<HintEntity,Long> {
    List<HintEntity> findByQuizSongRelation(QuizSongRelation quizSongRelation);
}
