package com.banquito.cards.seguridad.repository;

import com.banquito.cards.seguridad.model.LogConexion;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface LogConexionRepository extends JpaRepository<LogConexion, Integer> {

}
