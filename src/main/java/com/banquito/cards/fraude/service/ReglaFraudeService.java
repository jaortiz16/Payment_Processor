package com.banquito.cards.fraude.service;

import com.banquito.cards.fraude.model.ReglaFraude;
import com.banquito.cards.fraude.repository.ReglaFraudeRepository;
import com.banquito.cards.exception.NotFoundException;
import com.banquito.cards.exception.BusinessException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
public class ReglaFraudeService {

    private static final String ENTITY_NAME = "ReglaFraude";
    private final ReglaFraudeRepository reglaFraudeRepository;

    public ReglaFraudeService(ReglaFraudeRepository reglaFraudeRepository) {
        this.reglaFraudeRepository = reglaFraudeRepository;
    }

    @Transactional(readOnly = true)
    public List<ReglaFraude> obtenerReglasActivas() {
        log.info("Obteniendo reglas activas");
        return reglaFraudeRepository.findByEstadoOrderByPrioridadAsc(ReglaFraude.ESTADO_ACTIVO);
    }

    @Transactional(readOnly = true)
    public List<ReglaFraude> obtenerTodasLasReglas() {
        log.info("Obteniendo todas las reglas");
        return reglaFraudeRepository.findAll(Sort.by(Sort.Direction.ASC, "prioridad"));
    }

    @Transactional(readOnly = true)
    public Optional<ReglaFraude> obtenerReglaPorId(Integer id) {
        log.info("Buscando regla con ID: {}", id);
        return reglaFraudeRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<ReglaFraude> obtenerReglasPorTipo(String tipoRegla) {
        log.info("Buscando reglas por tipo: {}", tipoRegla);
        validarTipoRegla(tipoRegla);
        return reglaFraudeRepository.findByTipoReglaAndEstadoOrderByPrioridadAsc(tipoRegla, ReglaFraude.ESTADO_ACTIVO);
    }

    @Transactional(readOnly = true)
    public List<ReglaFraude> obtenerReglasPorNivelRiesgo(String nivelRiesgo) {
        validarNivelRiesgo(nivelRiesgo);
        return reglaFraudeRepository.findByNivelRiesgoAndEstado(nivelRiesgo, ReglaFraude.ESTADO_ACTIVO);
    }

    @Transactional(readOnly = true)
    public List<ReglaFraude> obtenerReglasPorPuntajeRiesgoMinimo(BigDecimal puntajeRiesgo) {
        validarPuntajeRiesgo(puntajeRiesgo);
        return reglaFraudeRepository.findByPuntajeRiesgoGreaterThanEqualAndEstado(
            puntajeRiesgo, ReglaFraude.ESTADO_ACTIVO);
    }

    @Transactional
    public ReglaFraude crearRegla(ReglaFraude regla) {
        log.info("Creando nueva regla de fraude");
        validarRegla(regla);
        regla.setFechaCreacion(LocalDateTime.now());
        regla.setEstado(ReglaFraude.ESTADO_ACTIVO);
        return reglaFraudeRepository.save(regla);
    }

    @Transactional
    public ReglaFraude actualizarRegla(Integer id, ReglaFraude regla) {
        log.info("Actualizando regla con ID: {}", id);
        ReglaFraude reglaExistente = obtenerReglaPorId(id)
                .orElseThrow(() -> new NotFoundException(id.toString(), ENTITY_NAME));
        
        validarRegla(regla);
        actualizarDatosRegla(reglaExistente, regla);
        
        return reglaFraudeRepository.save(reglaExistente);
    }

    @Transactional
    public void desactivarRegla(Integer id) {
        log.info("Desactivando regla con ID: {}", id);
        ReglaFraude regla = obtenerReglaPorId(id)
                .orElseThrow(() -> new NotFoundException(id.toString(), ENTITY_NAME));
        
        if (ReglaFraude.ESTADO_INACTIVO.equals(regla.getEstado())) {
            throw new BusinessException("La regla ya se encuentra inactiva", ENTITY_NAME, "desactivar");
        }
        
        regla.setEstado(ReglaFraude.ESTADO_INACTIVO);
        regla.setFechaActualizacion(LocalDateTime.now());
        regla.setUsuarioActualizacion("SYSTEM"); 
        
        reglaFraudeRepository.save(regla);
    }

    private void validarRegla(ReglaFraude regla) {
        if (regla == null) {
            throw new BusinessException("La regla no puede ser nula", ENTITY_NAME, "validar");
        }
        if (regla.getNombreRegla() == null || regla.getNombreRegla().trim().isEmpty()) {
            throw new BusinessException("El nombre de la regla es requerido", ENTITY_NAME, "validar");
        }
        validarTipoRegla(regla.getTipoRegla());
        validarNivelRiesgo(regla.getNivelRiesgo());
    }

    private void validarTipoRegla(String tipoRegla) {
        if (!List.of("TRX", "MNT", "GEO", "COM", "HOR").contains(tipoRegla)) {
            throw new BusinessException(tipoRegla, ENTITY_NAME, "tipo de regla inválido");
        }
    }

    private void validarNivelRiesgo(String nivelRiesgo) {
        if (!List.of("BAJ", "MED", "ALT").contains(nivelRiesgo)) {
            throw new BusinessException(nivelRiesgo, ENTITY_NAME, "nivel de riesgo inválido");
        }
    }

    private void validarPuntajeRiesgo(BigDecimal puntajeRiesgo) {
        if (puntajeRiesgo != null && 
            (puntajeRiesgo.compareTo(BigDecimal.ZERO) < 0 || puntajeRiesgo.compareTo(new BigDecimal("100")) > 0)) {
            throw new IllegalArgumentException("El puntaje de riesgo debe estar entre 0 y 100");
        }
    }

    private void actualizarDatosRegla(ReglaFraude reglaExistente, ReglaFraude regla) {
        reglaExistente.setNombreRegla(regla.getNombreRegla());
        reglaExistente.setDescripcion(regla.getDescripcion());
        reglaExistente.setTipoRegla(regla.getTipoRegla());
        reglaExistente.setLimiteTransacciones(regla.getLimiteTransacciones());
        reglaExistente.setPeriodoTiempo(regla.getPeriodoTiempo());
        reglaExistente.setLimiteMontoTotal(regla.getLimiteMontoTotal());
        reglaExistente.setPaisesPermitidos(regla.getPaisesPermitidos());
        reglaExistente.setComerciosExcluidos(regla.getComerciosExcluidos());
        reglaExistente.setHoraInicio(regla.getHoraInicio());
        reglaExistente.setHoraFin(regla.getHoraFin());
        reglaExistente.setPuntajeRiesgo(regla.getPuntajeRiesgo());
        reglaExistente.setNivelRiesgo(regla.getNivelRiesgo());
        reglaExistente.setPrioridad(regla.getPrioridad());
        reglaExistente.setFechaActualizacion(LocalDateTime.now());
        reglaExistente.setUsuarioActualizacion("SYSTEM");
    }
}
