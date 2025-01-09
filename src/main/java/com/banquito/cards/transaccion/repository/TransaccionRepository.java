package com.banquito.cards.transaccion.repository;

import com.banquito.cards.comision.model.Banco;
import com.banquito.cards.transaccion.model.Transaccion;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransaccionRepository extends JpaRepository<Transaccion, Integer> {
    
    List<Transaccion> findByEstadoAndFechaCreacionBetween(String estado, LocalDateTime fechaInicio, LocalDateTime fechaFin);
    
    List<Transaccion> findByBancoCodigoAndMontoBetween(Integer codigoBanco, BigDecimal montoMinimo, BigDecimal montoMaximo);
    
    List<Transaccion> findByEstadoAndBancoCodigo(String estado, Integer codigoBanco);
    
    List<Transaccion> findByMarcaAndFechaCreacionGreaterThanEqual(String marca, LocalDateTime fecha);

    List<Transaccion> findByNumeroTarjetaAndFechaCreacionBetween(String numeroTarjeta, LocalDateTime fechaInicio, LocalDateTime fechaFin);

    List<Transaccion> findByBancoAndFechaCreacionBetween(Banco banco, LocalDateTime fechaInicio, LocalDateTime fechaFin);

    Optional<Transaccion> findFirstByCodigoUnicoTransaccionOrderByFechaCreacionDesc(String codigoUnicoTransaccion);

    boolean existsByCodigoUnicoTransaccion(String codigoUnicoTransaccion);
}
