package com.banquito.cards.fraude.repository;

import com.banquito.cards.fraude.model.ReglaFraude;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReglaFraudeRepository extends JpaRepository<ReglaFraude, Integer> {
    
    List<ReglaFraude> findByEstado(String estado);
    
    List<ReglaFraude> findByEstadoOrderByPrioridadAsc(String estado);
    
    List<ReglaFraude> findByTipoReglaAndEstado(String tipoRegla, String estado);
}