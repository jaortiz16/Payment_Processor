package com.banquito.cards.transaccion.repository;

import com.banquito.cards.comision.model.Banco;
import com.banquito.cards.transaccion.model.Transaccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransaccionRepository extends JpaRepository<Transaccion, Integer> {
    List<Transaccion> findByBancoAndFechaCreacionBetween(
            Banco banco,
            LocalDateTime fechaInicio, 
            LocalDateTime fechaFin
    );
    
    List<Transaccion> findByNumeroTarjetaAndFechaCreacionBetween(
            String numeroTarjeta,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin
    );
}
