package com.banquito.cards.fraude.service;

import com.banquito.cards.fraude.model.MonitoreoFraude;
import com.banquito.cards.fraude.model.ReglaFraude;
import com.banquito.cards.fraude.repository.MonitoreoFraudeRepository;
import com.banquito.cards.transaccion.model.Transaccion;
import com.banquito.cards.transaccion.repository.TransaccionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.math.BigDecimal;

@Service
public class MonitoreoFraudeService {

    private final MonitoreoFraudeRepository monitoreoFraudeRepository;
    private final ReglaFraudeService reglaFraudeService;
    private final TransaccionRepository transaccionRepository;

    public MonitoreoFraudeService(MonitoreoFraudeRepository monitoreoFraudeRepository,
                                 ReglaFraudeService reglaFraudeService,
                                 TransaccionRepository transaccionRepository) {
        this.monitoreoFraudeRepository = monitoreoFraudeRepository;
        this.reglaFraudeService = reglaFraudeService;
        this.transaccionRepository = transaccionRepository;
    }

    @Transactional
    public String evaluarRiesgoTransaccion(Transaccion transaccion) {
        List<ReglaFraude> reglas = reglaFraudeService.obtenerTodasLasReglas();
        String nivelRiesgo = "BAJO";

        for (ReglaFraude regla : reglas) {
            if (Boolean.TRUE.equals(excedeLimiteTransacciones(transaccion, regla))) {
                registrarAlerta(transaccion, regla, "ALTO");
                return "ALTO";
            }
        }

        return nivelRiesgo;
    }

    private Boolean excedeLimiteTransacciones(Transaccion transaccion, ReglaFraude regla) {
        LocalDateTime fechaInicio = obtenerFechaInicioPeriodo(regla.getPeriodoTiempo());
        LocalDateTime fechaFin = LocalDateTime.now();

        // Verificar límites por banco
        List<Transaccion> transaccionesBanco = transaccionRepository.findByBancoAndFechaCreacionBetween(
                transaccion.getBanco(),
                fechaInicio,
                fechaFin
        );

        // Verificar límites por tarjeta
        List<Transaccion> transaccionesTarjeta = transaccionRepository.findByNumeroTarjetaAndFechaCreacionBetween(
                transaccion.getNumeroTarjeta(),
                fechaInicio,
                fechaFin
        );

        BigDecimal montoTotalBanco = calcularMontoTotal(transaccionesBanco);
        BigDecimal montoTotalTarjeta = calcularMontoTotal(transaccionesTarjeta);

        // Límites por tarjeta (puedes ajustar estos valores)
        int maxTransaccionesPorTarjeta = 5; // máximo 5 transacciones por período
        BigDecimal maxMontoPorTarjeta = new BigDecimal("3000.00"); // máximo 3000 por período

        return transaccionesBanco.size() > regla.getLimiteTransacciones().intValue() ||
               montoTotalBanco.compareTo(regla.getLimiteMontoTotal()) > 0 ||
               transaccionesTarjeta.size() > maxTransaccionesPorTarjeta ||
               montoTotalTarjeta.compareTo(maxMontoPorTarjeta) > 0;
    }

    private BigDecimal calcularMontoTotal(List<Transaccion> transacciones) {
        return transacciones.stream()
                .map(Transaccion::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private LocalDateTime obtenerFechaInicioPeriodo(String periodoTiempo) {
        LocalDateTime ahora = LocalDateTime.now();
        return switch (periodoTiempo) {
            case "DIA" -> ahora.minusDays(1);
            case "SEM" -> ahora.minusWeeks(1);
            case "MES" -> ahora.minusMonths(1);
            default -> throw new IllegalArgumentException("Periodo de tiempo no válido: " + periodoTiempo);
        };
    }

    private void registrarAlerta(Transaccion transaccion, ReglaFraude regla, String nivelRiesgo) {
        MonitoreoFraude monitoreo = new MonitoreoFraude();
        monitoreo.setReglaFraude(regla);
        monitoreo.setRiesgo(nivelRiesgo);
        monitoreo.setFechaDeteccion(LocalDateTime.now());
        monitoreoFraudeRepository.save(monitoreo);
    }
}
