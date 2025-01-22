package com.banquito.cards.transaccion.repository;

import com.banquito.cards.transaccion.model.Transaccion;
import org.springframework.data.jpa.repository.JpaRepository;
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

    Optional<Transaccion> findFirstByCodigoUnicoTransaccionOrderByFechaCreacionDesc(
            String codigoUnicoTransaccion);

    boolean existsByCodigoUnicoTransaccion(String codigoUnicoTransaccion);
}
