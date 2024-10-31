package com.CAUCSD.MUTCHIGI.quizSong.hint;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HintRepository extends JpaRepository<HintEntity,Long> {
}
