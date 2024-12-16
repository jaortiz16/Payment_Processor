package com.banquito.cards.fraude.repository;

import com.banquito.cards.fraude.model.MonitoreoFraude;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MonitoreoFraudeRepository extends JpaRepository<MonitoreoFraude, Integer> {

}