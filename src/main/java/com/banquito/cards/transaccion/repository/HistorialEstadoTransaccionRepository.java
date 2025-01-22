package com.banquito.cards.transaccion.repository;

import com.banquito.cards.transaccion.model.HistorialEstadoTransaccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HistorialEstadoTransaccionRepository extends JpaRepository<HistorialEstadoTransaccion, Integer> {
    
    List<HistorialEstadoTransaccion> findByTransaccionCodigoOrderByFechaEstadoCambioDesc(Integer codigoTransaccion);
    
    List<HistorialEstadoTransaccion> findByEstadoAndFechaEstadoCambioBetweenOrderByFechaEstadoCambioDesc(
            String estado, LocalDateTime fechaInicio, LocalDateTime fechaFin);
            
    List<HistorialEstadoTransaccion> findByFechaEstadoCambioBetweenOrderByFechaEstadoCambioDesc(
            LocalDateTime fechaInicio, LocalDateTime fechaFin);
}
