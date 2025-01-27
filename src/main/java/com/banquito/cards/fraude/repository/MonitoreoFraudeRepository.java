package com.banquito.cards.fraude.repository;

import com.banquito.cards.fraude.model.MonitoreoFraude;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MonitoreoFraudeRepository extends JpaRepository<MonitoreoFraude, String> {
    
    List<MonitoreoFraude> findByEstado(String estado);
    
    List<MonitoreoFraude> findByFechaDeteccionBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);
    
    List<MonitoreoFraude> findByTransaccionCodigo(Integer codigoTransaccion);
    
    List<MonitoreoFraude> findByTransaccionNumeroTarjetaAndFechaDeteccionBetween(
        String numeroTarjeta, LocalDateTime fechaInicio, LocalDateTime fechaFin);
    
    Optional<MonitoreoFraude> findByCodigoUnicoTransaccion(String codigoUnicoTransaccion);
    
    List<MonitoreoFraude> findByNivelRiesgoAndEstado(String nivelRiesgo, String estado);
    
    List<MonitoreoFraude> findByPuntajeRiesgoGreaterThanEqualAndEstado(
        java.math.BigDecimal puntajeRiesgo, String estado);
    
    List<MonitoreoFraude> findByRequiereVerificacionAdicionalAndEstado(
        Boolean requiereVerificacion, String estado);
    
    List<MonitoreoFraude> findByIpOrigenAndFechaDeteccionBetween(
        String ipOrigen, LocalDateTime fechaInicio, LocalDateTime fechaFin);
    
    List<MonitoreoFraude> findByUbicacionGeograficaAndFechaDeteccionBetween(
        String ubicacionGeografica, LocalDateTime fechaInicio, LocalDateTime fechaFin);
    
    Optional<MonitoreoFraude> findFirstByCodigoUnicoTransaccionOrderByFechaDeteccionDesc(String codigoUnicoTransaccion);

    Page<MonitoreoFraude> findByEstadoAndNivelRiesgo(String estado, String nivelRiesgo, Pageable pageable);
    
    Page<MonitoreoFraude> findByEstado(String estado, Pageable pageable);
    
    Page<MonitoreoFraude> findByNivelRiesgo(String nivelRiesgo, Pageable pageable);
}