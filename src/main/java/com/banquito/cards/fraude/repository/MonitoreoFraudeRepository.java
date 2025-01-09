package com.banquito.cards.fraude.repository;

import com.banquito.cards.fraude.model.MonitoreoFraude;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MonitoreoFraudeRepository extends JpaRepository<MonitoreoFraude, Integer> {
    
    List<MonitoreoFraude> findByEstado(String estado);
    
    List<MonitoreoFraude> findByFechaDeteccionBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);
    
    List<MonitoreoFraude> findByTransaccionCodigo(Integer codigoTransaccion);
    
    List<MonitoreoFraude> findByTransaccionNumeroTarjetaAndFechaDeteccionBetween(
        String numeroTarjeta, LocalDateTime fechaInicio, LocalDateTime fechaFin);
    
    Optional<MonitoreoFraude> findByCodigoUnicoTransaccion(String codigoUnicoTransaccion);
}