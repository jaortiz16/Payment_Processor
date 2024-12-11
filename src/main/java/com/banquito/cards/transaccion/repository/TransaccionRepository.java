package com.banquito.cards.transaccion.repository;

import com.banquito.cards.transaccion.model.Transaccion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransaccionRepository extends JpaRepository<Transaccion, Integer> {
}
