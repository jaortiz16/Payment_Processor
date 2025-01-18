package com.banquito.cards.fraude.service;

import com.banquito.cards.fraude.model.ReglaFraude;
import com.banquito.cards.fraude.repository.ReglaFraudeRepository;
import com.banquito.cards.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
        return reglaFraudeRepository.findByEstadoOrderByPrioridadAsc(ReglaFraude.ESTADO_ACTIVO);
    }

    @Transactional(readOnly = true)
    public List<ReglaFraude> obtenerTodasLasReglas() {
        return reglaFraudeRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<ReglaFraude> obtenerReglaPorId(Integer id) {
        return reglaFraudeRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<ReglaFraude> obtenerReglasPorTipo(String tipoRegla) {
        validarTipoRegla(tipoRegla);
        return reglaFraudeRepository.findByTipoReglaAndEstado(tipoRegla, ReglaFraude.ESTADO_ACTIVO);
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
        validarRegla(regla);
        regla.setFechaCreacion(LocalDateTime.now());
        regla.setEstado(ReglaFraude.ESTADO_ACTIVO);
        return reglaFraudeRepository.save(regla);
    }

    public ReglaFraude actualizarRegla(Integer id, ReglaFraude regla) {
        ReglaFraude reglaExistente = obtenerReglaPorId(id)
                .orElseThrow(() -> new NotFoundException(id.toString(), ENTITY_NAME));
        
        validarRegla(regla);
        if (!reglaExistente.getNombreRegla().equals(regla.getNombreRegla()) &&
            reglaFraudeRepository.existsByNombreReglaAndEstado(regla.getNombreRegla(), ReglaFraude.ESTADO_ACTIVO)) {
            throw new IllegalArgumentException("Ya existe una regla activa con el nombre: " + regla.getNombreRegla());
        }
        
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
        reglaExistente.setUsuarioActualizacion("SYSTEM"); // TODO: Obtener usuario actual
        
        return reglaFraudeRepository.save(reglaExistente);
    }

    public void desactivarRegla(Integer id) {
        ReglaFraude regla = obtenerReglaPorId(id)
                .orElseThrow(() -> new NotFoundException(id.toString(), ENTITY_NAME));
        
        if (ReglaFraude.ESTADO_INACTIVO.equals(regla.getEstado())) {
            throw new IllegalArgumentException("La regla ya se encuentra inactiva");
        }
        
        regla.setEstado(ReglaFraude.ESTADO_INACTIVO);
        regla.setFechaActualizacion(LocalDateTime.now());
        regla.setUsuarioActualizacion("SYSTEM"); // TODO: Obtener usuario actual
        
        reglaFraudeRepository.save(regla);
    }

    private void validarRegla(ReglaFraude regla) {
        if (regla.getNombreRegla() == null || regla.getNombreRegla().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la regla es requerido");
        }
        if (regla.getDescripcion() == null || regla.getDescripcion().trim().isEmpty()) {
            throw new IllegalArgumentException("La descripción de la regla es requerida");
        }
        validarTipoRegla(regla.getTipoRegla());
        validarPeriodoTiempo(regla.getPeriodoTiempo());
        validarNivelRiesgo(regla.getNivelRiesgo());
        validarPuntajeRiesgo(regla.getPuntajeRiesgo());
        
        switch (regla.getTipoRegla()) {
            case ReglaFraude.TIPO_TRANSACCIONES:
                if (regla.getLimiteTransacciones() == null || regla.getLimiteTransacciones().compareTo(BigDecimal.ZERO) <= 0) {
                    throw new IllegalArgumentException("El límite de transacciones debe ser mayor a 0");
                }
                break;
            case ReglaFraude.TIPO_MONTO:
                if (regla.getLimiteMontoTotal() == null || regla.getLimiteMontoTotal().compareTo(BigDecimal.ZERO) <= 0) {
                    throw new IllegalArgumentException("El límite de monto total debe ser mayor a 0");
                }
                break;
            case ReglaFraude.TIPO_UBICACION:
                if (regla.getPaisesPermitidos() == null || regla.getPaisesPermitidos().trim().isEmpty()) {
                    throw new IllegalArgumentException("Debe especificar al menos un país permitido");
                }
                break;
            case ReglaFraude.TIPO_COMERCIO:
                if (regla.getComerciosExcluidos() == null || regla.getComerciosExcluidos().trim().isEmpty()) {
                    throw new IllegalArgumentException("Debe especificar al menos un comercio excluido");
                }
                break;
            case ReglaFraude.TIPO_HORARIO:
                if (regla.getHoraInicio() == null || regla.getHoraFin() == null) {
                    throw new IllegalArgumentException("Debe especificar hora de inicio y fin");
                }
                if (regla.getHoraInicio().isAfter(regla.getHoraFin())) {
                    throw new IllegalArgumentException("La hora de inicio debe ser anterior a la hora fin");
                }
                break;
        }
    }

    private void validarTipoRegla(String tipoRegla) {
        if (!List.of(
                ReglaFraude.TIPO_TRANSACCIONES,
                ReglaFraude.TIPO_MONTO,
                ReglaFraude.TIPO_UBICACION,
                ReglaFraude.TIPO_COMERCIO,
                ReglaFraude.TIPO_HORARIO
            ).contains(tipoRegla)) {
            throw new IllegalArgumentException("Tipo de regla inválido");
        }
    }

    private void validarPeriodoTiempo(String periodoTiempo) {
        if (!List.of(
                ReglaFraude.PERIODO_MINUTOS,
                ReglaFraude.PERIODO_HORAS,
                ReglaFraude.PERIODO_DIAS
            ).contains(periodoTiempo)) {
            throw new IllegalArgumentException("Periodo de tiempo inválido");
        }
    }

    private void validarNivelRiesgo(String nivelRiesgo) {
        if (!List.of(
                ReglaFraude.NIVEL_RIESGO_BAJO,
                ReglaFraude.NIVEL_RIESGO_MEDIO,
                ReglaFraude.NIVEL_RIESGO_ALTO
            ).contains(nivelRiesgo)) {
            throw new IllegalArgumentException("Nivel de riesgo inválido");
        }
    }

    private void validarPuntajeRiesgo(BigDecimal puntajeRiesgo) {
        if (puntajeRiesgo != null && 
            (puntajeRiesgo.compareTo(BigDecimal.ZERO) < 0 || puntajeRiesgo.compareTo(new BigDecimal("100")) > 0)) {
            throw new IllegalArgumentException("El puntaje de riesgo debe estar entre 0 y 100");
        }
    }
}
