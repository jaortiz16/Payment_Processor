package com.banquito.cards.fraude.repository;

import com.banquito.cards.fraude.model.MonitoreoFraude;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MonitoreoFraudeRepository extends JpaRepository<MonitoreoFraude, Integer> {
    List<MonitoreoFraude> findByReglaFraudeCodRegla(Integer reglaId);

    List<MonitoreoFraude> findByFechaDeteccionBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    List<MonitoreoFraude> findByRiesgo(String riesgo);
}