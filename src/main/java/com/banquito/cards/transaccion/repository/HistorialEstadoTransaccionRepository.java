package com.banquito.cards.transaccion.repository;

import com.banquito.cards.transaccion.model.HistorialEstadoTransaccion;
import com.banquito.cards.transaccion.model.Transaccion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    Page<HistorialEstadoTransaccion> findByTransaccion(Transaccion transaccion, Pageable pageable);
    
    Page<HistorialEstadoTransaccion> findByTransaccionAndEstado(Transaccion transaccion, String estado, Pageable pageable);
    
    Page<HistorialEstadoTransaccion> findByEstadoAndFechaEstadoCambioBetween(
            String estado, LocalDateTime fechaInicio, LocalDateTime fechaFin, Pageable pageable);
            
    Page<HistorialEstadoTransaccion> findByFechaEstadoCambioBetween(
            LocalDateTime fechaInicio, LocalDateTime fechaFin, Pageable pageable);
}
