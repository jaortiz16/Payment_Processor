package com.banquito.cards.fraude.repository;

import com.banquito.cards.fraude.model.ReglaFraude;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReglaFraudeRepository extends JpaRepository<ReglaFraude, Integer> {
}