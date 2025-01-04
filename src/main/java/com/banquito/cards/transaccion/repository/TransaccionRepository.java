package com.banquito.cards.transaccion.repository;

import com.banquito.cards.transaccion.model.Transaccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransaccionRepository extends JpaRepository<Transaccion, Integer> {
    
    List<Transaccion> findByEstadoAndFechaCreacionBetween(String estado, LocalDateTime fechaInicio, LocalDateTime fechaFin);
    
    List<Transaccion> findByBancoCodigoAndMontoBetween(Integer codigoBanco, BigDecimal montoMinimo, BigDecimal montoMaximo);
    
    List<Transaccion> findByEstadoAndBancoCodigo(String estado, Integer codigoBanco);
    
    List<Transaccion> findByMarcaAndFechaCreacionGreaterThanEqual(String marca, LocalDateTime fecha);
}
