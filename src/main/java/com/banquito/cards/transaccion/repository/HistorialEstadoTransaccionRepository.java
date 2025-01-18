package com.banquito.cards.transaccion.repository;

import com.banquito.cards.transaccion.model.HistorialEstadoTransaccion;
import com.banquito.cards.transaccion.model.Transaccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HistorialEstadoTransaccionRepository extends JpaRepository<HistorialEstadoTransaccion, Integer> {
    
    List<HistorialEstadoTransaccion> findByEstadoAndFechaEstadoCambioBetweenOrderByFechaEstadoCambioDesc(
            String estado, LocalDateTime fechaInicio, LocalDateTime fechaFin);

    List<HistorialEstadoTransaccion> findByFechaEstadoCambioBetweenOrderByFechaEstadoCambioDesc(
            LocalDateTime fechaInicio, LocalDateTime fechaFin);
    
    List<HistorialEstadoTransaccion> findByTransaccionOrderByFechaEstadoCambioDesc(
            Transaccion transaccion);
            
    @Query("SELECT h FROM HistorialEstadoTransaccion h WHERE h.transaccion.banco.codigo = :codigoBanco " +
           "AND h.fechaEstadoCambio BETWEEN :fechaInicio AND :fechaFin " +
           "ORDER BY h.fechaEstadoCambio DESC")
    List<HistorialEstadoTransaccion> buscarHistorialPorBancoYFecha(
            @Param("codigoBanco") Integer codigoBanco,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin);
            
    @Query("SELECT h FROM HistorialEstadoTransaccion h WHERE h.transaccion.codigoUnicoTransaccion = :codigoUnico " +
           "ORDER BY h.fechaEstadoCambio DESC")
    List<HistorialEstadoTransaccion> buscarHistorialPorCodigoUnicoTransaccion(
            @Param("codigoUnico") String codigoUnico);
}
