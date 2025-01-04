package com.banquito.cards.fraude.service;

import com.banquito.cards.fraude.model.MonitoreoFraude;
import com.banquito.cards.fraude.model.ReglaFraude;
import com.banquito.cards.fraude.repository.MonitoreoFraudeRepository;
import com.banquito.cards.transaccion.model.Transaccion;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.math.BigDecimal;

@Service
public class MonitoreoFraudeService {

    private final MonitoreoFraudeRepository monitoreoFraudeRepository;
    private final ReglaFraudeService reglaFraudeService;

    public MonitoreoFraudeService(MonitoreoFraudeRepository monitoreoFraudeRepository,
                                 ReglaFraudeService reglaFraudeService) {
        this.monitoreoFraudeRepository = monitoreoFraudeRepository;
        this.reglaFraudeService = reglaFraudeService;
    }

    @Transactional
    public String evaluarRiesgoTransaccion(Transaccion transaccion) {
        List<ReglaFraude> reglas = reglaFraudeService.obtenerTodasLasReglas();
        String nivelRiesgoMaximo = "BAJO";

        for (ReglaFraude regla : reglas) {
            String riesgoActual = evaluarRegla(transaccion, regla);
            if ("ALTO".equals(riesgoActual)) {
                registrarAlerta(regla, "ALTO");
                return "ALTO";
            } else if ("MEDIO".equals(riesgoActual) && !"ALTO".equals(nivelRiesgoMaximo)) {
                nivelRiesgoMaximo = "MEDIO";
            }
        }

        if (!"BAJO".equals(nivelRiesgoMaximo)) {
            registrarAlerta(reglas.get(0), nivelRiesgoMaximo);
        }

        return nivelRiesgoMaximo;
    }

    private String evaluarRegla(Transaccion transaccion, ReglaFraude regla) {
        if (transaccion.getMonto().compareTo(regla.getLimiteMontoTotal()) > 0) {
            return "ALTO";
        }
        if (excedeLimiteTransacciones(transaccion, regla)) {
            return "ALTO";
        }

        return "BAJO";
    }

    private boolean excedeLimiteTransacciones(Transaccion transaccion, ReglaFraude regla) {
        LocalDateTime fechaInicio = obtenerFechaInicioPeriodo(regla.getPeriodoTiempo());
        return false;
    }

    private LocalDateTime obtenerFechaInicioPeriodo(String periodoTiempo) {
        LocalDateTime ahora = LocalDateTime.now();
        return switch (periodoTiempo) {
            case "HOR" -> ahora.minusHours(1);
            case "DIA" -> ahora.minusDays(1);
            case "SEM" -> ahora.minusWeeks(1);
            default -> ahora.minusDays(1);
        };
    }

    @Transactional
    public MonitoreoFraude registrarAlerta(ReglaFraude regla, String riesgo) {
        MonitoreoFraude monitoreo = new MonitoreoFraude();
        monitoreo.setReglaFraude(regla);
        monitoreo.setRiesgo(riesgo);
        monitoreo.setFechaDeteccion(LocalDateTime.now());
        
        return monitoreoFraudeRepository.save(monitoreo);
    }
}
