package com.banquito.cards.fraude.repository;

import com.banquito.cards.fraude.model.MonitoreoFraude;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MonitoreoFraudeRepository extends JpaRepository<MonitoreoFraude, Integer> {
}