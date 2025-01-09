package com.banquito.cards.comision.repository;

import com.banquito.cards.comision.model.Comision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ComisionRepository extends JpaRepository<Comision, Integer> {
    
    List<Comision> findByTipo(String tipo);
    
    List<Comision> findByMontoBaseBetween(BigDecimal montoMinimo, BigDecimal montoMaximo);

}
