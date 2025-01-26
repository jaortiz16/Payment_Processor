package com.banquito.cards.fraude.repository;

import com.banquito.cards.fraude.model.ReglaFraude;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReglaFraudeRepository extends JpaRepository<ReglaFraude, Integer> {
    
    List<ReglaFraude> findByEstado(String estado);
    
    List<ReglaFraude> findByEstadoOrderByPrioridadAsc(String estado);
    
    List<ReglaFraude> findByTipoReglaAndEstado(String tipoRegla, String estado);
    
    Optional<ReglaFraude> findByNombreReglaAndEstado(String nombreRegla, String estado);
    
    List<ReglaFraude> findByNivelRiesgoAndEstado(String nivelRiesgo, String estado);
    
    List<ReglaFraude> findByTipoReglaAndEstadoOrderByPrioridadAsc(String tipoRegla, String estado);
    
    List<ReglaFraude> findByNivelRiesgoAndEstadoOrderByPrioridadAsc(String nivelRiesgo, String estado);
    
    boolean existsByNombreReglaAndEstado(String nombreRegla, String estado);
    
    List<ReglaFraude> findByPuntajeRiesgoGreaterThanEqualAndEstado(
            java.math.BigDecimal puntajeRiesgo, String estado);
}