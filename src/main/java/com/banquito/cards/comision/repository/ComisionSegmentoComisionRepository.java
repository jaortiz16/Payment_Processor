package com.banquito.cards.comision.repository;

import com.banquito.cards.comision.model.ComisionPK;
import com.banquito.cards.comision.model.ComisionSegmento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComisionSegmentoComisionRepository extends JpaRepository<ComisionSegmento, ComisionPK> {
}
