package com.banquito.cards.transaccion.repository;

import com.banquito.cards.transaccion.model.HistorialEstadoTransaccion;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface HistorialEstadoTransaccionRepository extends JpaRepository<HistorialEstadoTransaccion, Integer> {

}
