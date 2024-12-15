package com.banquito.cards.comision.repository;

import com.banquito.cards.comision.model.Comision;
import com.banquito.cards.comision.model.ComisionPK;
import com.banquito.cards.comision.model.ComisionSegmento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComisionSegmentoRepository extends JpaRepository<ComisionSegmento, ComisionPK> {
    List<ComisionSegmento> findByComision(Comision comision);
}