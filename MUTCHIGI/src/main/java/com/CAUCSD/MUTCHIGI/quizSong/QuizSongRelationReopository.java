package com.CAUCSD.MUTCHIGI.quizSong;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizSongRelationReopository extends JpaRepository<QuizSongRelation, Long> {
    QuizSongRelation findByQuizEntity_QuizIdAndSongEntity_SongId(Long quizId, Long songId);
}
