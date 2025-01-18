package com.banquito.cards.comision.repository;

import com.banquito.cards.comision.model.Comision;
import com.banquito.cards.comision.model.ComisionPK;
import com.banquito.cards.comision.model.ComisionSegmento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ComisionSegmentoRepository extends JpaRepository<ComisionSegmento, ComisionPK> {
    
    List<ComisionSegmento> findByComision(Comision comision);
    
    List<ComisionSegmento> findByComisionOrderByPkCodSegmentoAsc(Comision comision);
    
    Optional<ComisionSegmento> findByComisionAndTransaccionesHasta(Comision comision, BigDecimal transaccionesHasta);
    
    boolean existsByComisionAndTransaccionesHasta(Comision comision, BigDecimal transaccionesHasta);
}