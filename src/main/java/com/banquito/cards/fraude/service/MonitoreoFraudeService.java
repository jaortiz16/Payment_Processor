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

    private String evaluarRegla(Transaccion transaccion, ReglaFraude regla) {
        // Evaluar límite de monto
        if (transaccion.getMonto().compareTo(regla.getLimiteMontoTotal()) > 0) {
            return "ALTO";
        }
        
        // Aquí se pueden agregar más validaciones según el tipo de regla
        // Por ejemplo, verificar cantidad de transacciones en un período
        // o validar ubicación geográfica
        
        return "BAJO";
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
