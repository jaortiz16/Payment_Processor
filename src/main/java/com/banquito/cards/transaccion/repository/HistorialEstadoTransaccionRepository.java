package com.banquito.cards.transaccion.repository;

import com.banquito.cards.transaccion.model.HistorialEstadoTransaccion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistorialEstadoTransaccionRepository extends JpaRepository<HistorialEstadoTransaccion, Integer> {
}
