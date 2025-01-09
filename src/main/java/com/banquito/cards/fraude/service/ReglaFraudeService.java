package com.banquito.cards.fraude.service;

import com.banquito.cards.fraude.model.ReglaFraude;
import com.banquito.cards.fraude.repository.ReglaFraudeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReglaFraudeService {

    private final ReglaFraudeRepository reglaFraudeRepository;

    public ReglaFraudeService(ReglaFraudeRepository reglaFraudeRepository) {
        this.reglaFraudeRepository = reglaFraudeRepository;
    }

    public List<ReglaFraude> obtenerTodasLasReglas() {
        return reglaFraudeRepository.findAll();
    }

    public List<ReglaFraude> obtenerReglasActivas() {
        return reglaFraudeRepository.findByEstado("ACT");
    }

    public Optional<ReglaFraude> obtenerReglaPorId(Integer id) {
        return reglaFraudeRepository.findById(id);
    }

    @Transactional
    public ReglaFraude crearRegla(ReglaFraude regla) {
        validarRegla(regla);
        regla.setFechaCreacion(LocalDateTime.now());
        regla.setFechaActualizacion(LocalDateTime.now());
        regla.setEstado("ACT");
        return reglaFraudeRepository.save(regla);
    }

    @Transactional
    public ReglaFraude actualizarRegla(Integer id, ReglaFraude regla) {
        ReglaFraude reglaExistente = reglaFraudeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Regla de fraude no encontrada"));

        validarRegla(regla);
        
        reglaExistente.setNombreRegla(regla.getNombreRegla());
        reglaExistente.setTipoRegla(regla.getTipoRegla());
        reglaExistente.setLimiteTransacciones(regla.getLimiteTransacciones());
        reglaExistente.setPeriodoTiempo(regla.getPeriodoTiempo());
        reglaExistente.setLimiteMontoTotal(regla.getLimiteMontoTotal());
        reglaExistente.setNivelRiesgo(regla.getNivelRiesgo());
        reglaExistente.setPrioridad(regla.getPrioridad());
        reglaExistente.setFechaActualizacion(LocalDateTime.now());

        return reglaFraudeRepository.save(reglaExistente);
    }

    @Transactional
    public void desactivarRegla(Integer id) {
        ReglaFraude regla = reglaFraudeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Regla de fraude no encontrada"));

        if ("INA".equals(regla.getEstado())) {
            throw new RuntimeException("La regla ya está inactiva");
        }

        regla.setEstado("INA");
        regla.setFechaActualizacion(LocalDateTime.now());
        reglaFraudeRepository.save(regla);
    }

    private void validarRegla(ReglaFraude regla) {
        if (regla.getNombreRegla() == null || regla.getNombreRegla().trim().isEmpty()) {
            throw new RuntimeException("El nombre de la regla es requerido");
        }
        if (regla.getTipoRegla() == null || regla.getTipoRegla().trim().isEmpty()) {
            throw new RuntimeException("El tipo de regla es requerido");
        }
        if (regla.getNivelRiesgo() == null || regla.getNivelRiesgo().trim().isEmpty()) {
            throw new RuntimeException("El nivel de riesgo es requerido");
        }
        if (regla.getPeriodoTiempo() == null || regla.getPeriodoTiempo().trim().isEmpty()) {
            throw new RuntimeException("El periodo de tiempo es requerido");
        }

        switch (regla.getTipoRegla()) {
            case "MNT":
                if (regla.getLimiteMontoTotal() == null) {
                    throw new RuntimeException("El límite de monto total es requerido para reglas de tipo monto");
                }
                if (regla.getLimiteMontoTotal().compareTo(BigDecimal.ZERO) <= 0) {
                    throw new RuntimeException("El límite de monto total debe ser mayor a cero");
                }
                break;
            case "TRX":
                if (regla.getLimiteTransacciones() == null) {
                    throw new RuntimeException("El límite de transacciones es requerido para reglas de tipo transacción");
                }
                if (regla.getLimiteTransacciones().compareTo(BigDecimal.ZERO) <= 0) {
                    throw new RuntimeException("El límite de transacciones debe ser mayor a cero");
                }
                break;
            case "UBI":
                // Para reglas de ubicación no se requieren límites específicos
                break;
            default:
                throw new RuntimeException("Tipo de regla no válido. Use: MNT, TRX o UBI");
        }

        if (!List.of("BAJ", "MED", "ALT").contains(regla.getNivelRiesgo())) {
            throw new RuntimeException("Nivel de riesgo no válido. Use: BAJ, MED o ALT");
        }

        if (!List.of("HOR", "DIA", "SEM").contains(regla.getPeriodoTiempo())) {
            throw new RuntimeException("Periodo de tiempo no válido. Use: HOR, DIA o SEM");
        }
    }
}
