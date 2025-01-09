package com.banquito.cards.fraude.controller;

import com.banquito.cards.fraude.model.MonitoreoFraude;
import com.banquito.cards.fraude.service.MonitoreoFraudeService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/monitoreo-fraude")
public class MonitoreoFraudeController {

    private final MonitoreoFraudeService monitoreoFraudeService;

    public MonitoreoFraudeController(MonitoreoFraudeService monitoreoFraudeService) {
        this.monitoreoFraudeService = monitoreoFraudeService;
    }

    @GetMapping("/alertas/pendientes")
    public ResponseEntity<?> obtenerAlertasPendientes() {
        try {
            List<MonitoreoFraude> alertas = monitoreoFraudeService.obtenerAlertasPendientes();
            return ResponseEntity.ok(alertas);
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/alertas/por-fecha")
    public ResponseEntity<?> obtenerAlertasPorFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        try {
            List<MonitoreoFraude> alertas = monitoreoFraudeService.obtenerAlertasPorFecha(fechaInicio, fechaFin);
            return ResponseEntity.ok(alertas);
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/alertas/por-transaccion/{codTransaccion}")
    public ResponseEntity<?> obtenerAlertasPorTransaccion(@PathVariable Integer codTransaccion) {
        try {
            List<MonitoreoFraude> alertas = monitoreoFraudeService.obtenerAlertasPorTransaccion(codTransaccion);
            return ResponseEntity.ok(alertas);
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/alertas/{id}/procesar")
    public ResponseEntity<?> procesarAlerta(
            @PathVariable Integer id,
            @RequestParam String estado,
            @RequestParam(required = false) String detalle) {
        try {
            monitoreoFraudeService.procesarAlerta(id, estado, detalle != null ? detalle : "Alerta procesada");
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Alerta procesada exitosamente");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
} 