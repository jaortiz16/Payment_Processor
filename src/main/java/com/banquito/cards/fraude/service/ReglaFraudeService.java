package com.banquito.cards.fraude.service;

import com.banquito.cards.fraude.model.ReglaFraude;
import com.banquito.cards.fraude.repository.ReglaFraudeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        // Implementar método en repository para obtener reglas activas
        return reglaFraudeRepository.findByEstadoOrderByPrioridadDesc("ACT");
    }

    public Optional<ReglaFraude> obtenerReglaPorId(Integer id) {
        return reglaFraudeRepository.findById(id);
    }

    @Transactional
    public ReglaFraude crearRegla(ReglaFraude regla) {
        validarRegla(regla);
        regla.setFechaCreacion(LocalDateTime.now());
        regla.setFechaActualizacion(LocalDateTime.now());
        return reglaFraudeRepository.save(regla);
    }

    @Transactional
    public ReglaFraude actualizarRegla(Integer id, ReglaFraude regla) {
        ReglaFraude reglaExistente = reglaFraudeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Regla de fraude no encontrada"));

        validarRegla(regla);
        
        reglaExistente.setNombreRegla(regla.getNombreRegla());
        reglaExistente.setLimiteTransacciones(regla.getLimiteTransacciones());
        reglaExistente.setPeriodoTiempo(regla.getPeriodoTiempo());
        reglaExistente.setLimiteMontoTotal(regla.getLimiteMontoTotal());
        reglaExistente.setFechaActualizacion(LocalDateTime.now());

        return reglaFraudeRepository.save(reglaExistente);
    }

    @Transactional
    public void desactivarRegla(Integer id) {
        ReglaFraude regla = reglaFraudeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Regla de fraude no encontrada"));

        regla.setFechaActualizacion(LocalDateTime.now());
        reglaFraudeRepository.save(regla);
    }

    private void validarRegla(ReglaFraude regla) {
        if (regla.getLimiteTransacciones() == null || regla.getLimiteTransacciones().signum() <= 0) {
            throw new IllegalArgumentException("El límite de transacciones debe ser mayor a cero");
        }
        if (regla.getLimiteMontoTotal() == null || regla.getLimiteMontoTotal().signum() <= 0) {
            throw new IllegalArgumentException("El límite de monto total debe ser mayor a cero");
        }
        if (!periodoTiempoValido(regla.getPeriodoTiempo())) {
            throw new IllegalArgumentException("Periodo de tiempo inválido. Use: DIA, HOR, SEM");
        }
    }

    private boolean periodoTiempoValido(String periodoTiempo) {
        return periodoTiempo != null && 
               (periodoTiempo.equals("DIA") || 
                periodoTiempo.equals("HOR") || 
                periodoTiempo.equals("SEM"));
    }
}
