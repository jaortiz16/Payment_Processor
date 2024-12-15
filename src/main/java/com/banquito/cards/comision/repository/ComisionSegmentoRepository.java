package com.banquito.cards.comision.repository;

import com.banquito.cards.comision.model.ComisionPK;
import com.banquito.cards.comision.model.ComisionSegmento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComisionSegmentoRepository extends JpaRepository<ComisionSegmento, ComisionPK> {
    List<ComisionSegmento> findByComisionCodComision(String comisionId);
}