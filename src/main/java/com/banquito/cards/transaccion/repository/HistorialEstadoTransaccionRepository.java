package com.banquito.cards.transaccion.repository;

import com.banquito.cards.transaccion.model.HistorialEstadoTransaccion;
import com.banquito.cards.transaccion.model.Transaccion;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HistorialEstadoTransaccionRepository extends JpaRepository<HistorialEstadoTransaccion, Integer> {
    
    List<HistorialEstadoTransaccion> findByEstadoAndFechaEstadoCambioBetween(String estado, LocalDateTime fechaInicio, LocalDateTime fechaFin);

    List<HistorialEstadoTransaccion> findByFechaEstadoCambioBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);
    
    List<HistorialEstadoTransaccion> findByTransaccionOrderByFechaEstadoCambioDesc(Transaccion transaccion);
}
