package com.banquito.cards.seguridad.repository;

import com.banquito.cards.seguridad.model.LogConexion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface LogConexionRepository extends JpaRepository<LogConexion, Integer> {
    List<LogConexion> findBySeguridadBancoCode(Integer codBanco);

    List<LogConexion> findBySeguridadMarcaMarca(String marca);

    List<LogConexion> findByFechaBetweenOrderByFechaDesc(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    List<LogConexion> findByResultado(String resultado);

    List<LogConexion> findByOperacion(String operacion);
}
