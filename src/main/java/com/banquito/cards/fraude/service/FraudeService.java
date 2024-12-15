package com.banquito.cards.fraude.service;

import com.banquito.cards.fraude.model.MonitoreoFraude;
import com.banquito.cards.fraude.model.ReglaFraude;
import com.banquito.cards.fraude.repository.MonitoreoFraudeRepository;
import com.banquito.cards.fraude.repository.ReglaFraudeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class FraudeService {

    private final ReglaFraudeRepository reglaFraudeRepository;
    private final MonitoreoFraudeRepository monitoreoFraudeRepository;

    public FraudeService(ReglaFraudeRepository reglaFraudeRepository,
                        MonitoreoFraudeRepository monitoreoFraudeRepository) {
        this.reglaFraudeRepository = reglaFraudeRepository;
        this.monitoreoFraudeRepository = monitoreoFraudeRepository;
    }

    public List<ReglaFraude> obtenerTodasLasReglas() {
        return this.reglaFraudeRepository.findAll();
    }

    public Optional<ReglaFraude> obtenerReglaPorId(Integer id) {
        return this.reglaFraudeRepository.findById(id);
    }

    @Transactional
    public ReglaFraude crearRegla(ReglaFraude regla) {
        regla.setFechaCreacion(LocalDateTime.now());
        regla.setFechaActualizacion(LocalDateTime.now());
        return this.reglaFraudeRepository.save(regla);
    }

    @Transactional
    public ReglaFraude actualizarRegla(Integer id, ReglaFraude regla) {
        ReglaFraude reglaExistente = this.reglaFraudeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Regla de fraude no encontrada"));
        
        reglaExistente.setNombreRegla(regla.getNombreRegla());
        reglaExistente.setLimiteTransacciones(regla.getLimiteTransacciones());
        reglaExistente.setPeriodoTiempo(regla.getPeriodoTiempo());
        reglaExistente.setLimiteMontoTotal(regla.getLimiteMontoTotal());
        reglaExistente.setFechaActualizacion(LocalDateTime.now());
        
        return this.reglaFraudeRepository.save(reglaExistente);
    }

    public List<MonitoreoFraude> obtenerMonitoreosPorRegla(Integer reglaId) {
        return this.monitoreoFraudeRepository.findByReglaFraudeCodRegla(reglaId);
    }

    @Transactional
    public MonitoreoFraude registrarMonitoreo(Integer reglaId, String riesgo) {
        ReglaFraude regla = this.reglaFraudeRepository.findById(reglaId)
            .orElseThrow(() -> new RuntimeException("Regla de fraude no encontrada"));
        
        MonitoreoFraude monitoreo = new MonitoreoFraude();
        monitoreo.setReglaFraude(regla);
        monitoreo.setRiesgo(riesgo);
        monitoreo.setFechaDeteccion(LocalDateTime.now());
        
        return this.monitoreoFraudeRepository.save(monitoreo);
    }
} 