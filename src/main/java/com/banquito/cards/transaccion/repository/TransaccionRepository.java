package com.banquito.cards.transaccion.repository;

import com.banquito.cards.transaccion.model.Transaccion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransaccionRepository extends JpaRepository<Transaccion, Integer> {
    List<Transaccion> findByEstado(String estado);

    List<Transaccion> findByBancoCodBanco(Integer codBanco);
}
