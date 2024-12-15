package com.banquito.cards.fraude.controller;

import com.banquito.cards.fraude.model.MonitoreoFraude;
import com.banquito.cards.fraude.service.MonitoreoFraudeService;
import com.banquito.cards.transaccion.model.Transaccion;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/monitoreo-fraude")
public class MonitoreoFraudeController {

    private final MonitoreoFraudeService monitoreoFraudeService;

    public MonitoreoFraudeController(MonitoreoFraudeService monitoreoFraudeService) {
        this.monitoreoFraudeService = monitoreoFraudeService;
    }

    @PostMapping("/evaluar")
    public ResponseEntity<MonitoreoFraude> evaluarTransaccion(@RequestBody Transaccion transaccion) {
        MonitoreoFraude alerta = monitoreoFraudeService.evaluarTransaccion(transaccion);
        if (alerta != null) {
            return ResponseEntity.ok(alerta);
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/alertas/fecha")
    public ResponseEntity<List<MonitoreoFraude>> obtenerAlertasPorFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        return ResponseEntity.ok(monitoreoFraudeService.obtenerAlertasPorFecha(fechaInicio, fechaFin));
    }

    @GetMapping("/alertas/riesgo/{nivel}")
    public ResponseEntity<List<MonitoreoFraude>> obtenerAlertasPorNivelRiesgo(
            @PathVariable String nivel) {
        return ResponseEntity.ok(monitoreoFraudeService.obtenerAlertasPorNivelRiesgo(nivel));
    }
} 