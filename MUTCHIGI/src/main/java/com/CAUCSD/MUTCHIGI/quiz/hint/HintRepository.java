package com.CAUCSD.MUTCHIGI.quiz.hint;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HintRepository extends JpaRepository<HintEntity,Long> {
}
