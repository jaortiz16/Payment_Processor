package com.banquito.cards.transaccion.repository;

import com.banquito.cards.comision.model.Banco;
import com.banquito.cards.transaccion.model.Transaccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransaccionRepository extends JpaRepository<Transaccion, Integer> {
    
    List<Transaccion> findByEstadoAndFechaCreacionBetweenOrderByFechaCreacionDesc(
            String estado, LocalDateTime fechaInicio, LocalDateTime fechaFin);
    
    List<Transaccion> findByBancoCodigoAndMontoBetweenOrderByMontoDesc(
            Integer codigoBanco, BigDecimal montoMinimo, BigDecimal montoMaximo);
    
    List<Transaccion> findByEstadoAndBancoCodigoOrderByFechaCreacionDesc(
            String estado, Integer codigoBanco);
    
    List<Transaccion> findByMarcaAndFechaCreacionGreaterThanEqualOrderByFechaCreacionDesc(
            String marca, LocalDateTime fecha);

    List<Transaccion> findByNumeroTarjetaAndFechaCreacionBetweenOrderByFechaCreacionDesc(
            String numeroTarjeta, LocalDateTime fechaInicio, LocalDateTime fechaFin);

    List<Transaccion> findByBancoAndFechaCreacionBetweenOrderByFechaCreacionDesc(
            Banco banco, LocalDateTime fechaInicio, LocalDateTime fechaFin);

    Optional<Transaccion> findFirstByCodigoUnicoTransaccionOrderByFechaCreacionDesc(
            String codigoUnicoTransaccion);

    boolean existsByCodigoUnicoTransaccion(String codigoUnicoTransaccion);
    
    @Query("SELECT t FROM Transaccion t WHERE t.estado = :estado " +
           "AND t.monto >= :montoMinimo AND t.fechaCreacion >= :fechaInicio")
    List<Transaccion> buscarTransaccionesParaMonitoreo(
            @Param("estado") String estado,
            @Param("montoMinimo") BigDecimal montoMinimo,
            @Param("fechaInicio") LocalDateTime fechaInicio);
            
    @Query("SELECT t FROM Transaccion t WHERE t.modalidad = :modalidad " +
           "AND t.fechaEjecucionRecurrencia <= :fechaActual " +
           "AND t.fechaFinRecurrencia >= :fechaActual")
    List<Transaccion> buscarTransaccionesRecurrentesPendientes(
            @Param("modalidad") String modalidad,
            @Param("fechaActual") LocalDateTime fechaActual);
            
    List<Transaccion> findByPaisAndEstadoAndFechaCreacionBetweenOrderByFechaCreacionDesc(
            String pais, String estado, LocalDateTime fechaInicio, LocalDateTime fechaFin);
}
