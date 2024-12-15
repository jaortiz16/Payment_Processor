package com.banquito.cards.comision.repository;

import com.banquito.cards.comision.model.Comision;
import com.banquito.cards.comision.model.ComisionPK;
import com.banquito.cards.comision.model.ComisionSegmento;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComisionSegmentoRepository extends JpaRepository<ComisionSegmento, ComisionPK> {

}