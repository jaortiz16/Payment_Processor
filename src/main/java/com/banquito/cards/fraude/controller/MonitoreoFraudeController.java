package com.banquito.cards.fraude.controller;

import com.banquito.cards.fraude.model.MonitoreoFraude;
import com.banquito.cards.fraude.service.MonitoreoFraudeService;
import com.banquito.cards.exception.NotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/monitoreo-fraude")
@Validated
public class MonitoreoFraudeController {

    private static final String ENTITY_NAME = "MonitoreoFraude";
    private final MonitoreoFraudeService monitoreoFraudeService;

    public MonitoreoFraudeController(MonitoreoFraudeService monitoreoFraudeService) {
        this.monitoreoFraudeService = monitoreoFraudeService;
    }

    @GetMapping("/alertas/pendientes")
    public ResponseEntity<List<MonitoreoFraude>> obtenerAlertasFraudePendientes() {
        try {
            List<MonitoreoFraude> alertas = monitoreoFraudeService.obtenerAlertasPendientes();
            return ResponseEntity.ok(alertas);
        } catch (RuntimeException e) {
            throw new RuntimeException("Error al obtener alertas pendientes: " + e.getMessage());
        }
    }

    @GetMapping("/alertas/por-fecha")
    public ResponseEntity<List<MonitoreoFraude>> obtenerAlertasFraudePorFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        try {
            List<MonitoreoFraude> alertas = monitoreoFraudeService.obtenerAlertasPorFecha(fechaInicio, fechaFin);
            return ResponseEntity.ok(alertas);
        } catch (RuntimeException e) {
            throw new RuntimeException("Error al obtener alertas por fecha: " + e.getMessage());
        }
    }

    @GetMapping("/alertas/por-transaccion/{codTransaccion}")
    public ResponseEntity<List<MonitoreoFraude>> obtenerAlertasFraudePorTransaccion(@PathVariable Integer codTransaccion) {
        try {
            List<MonitoreoFraude> alertas = monitoreoFraudeService.obtenerAlertasPorTransaccion(codTransaccion);
            return ResponseEntity.ok(alertas);
        } catch (RuntimeException e) {
            throw new RuntimeException("Error al obtener alertas por transacción: " + e.getMessage());
        }
    }

    @GetMapping("/alertas/por-tarjeta/{numeroTarjeta}")
    public ResponseEntity<List<MonitoreoFraude>> obtenerAlertasFraudePorTarjeta(
            @PathVariable @NotBlank String numeroTarjeta,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        try {
            List<MonitoreoFraude> alertas = monitoreoFraudeService.obtenerAlertasPorTarjeta(numeroTarjeta, fechaInicio, fechaFin);
            return ResponseEntity.ok(alertas);
        } catch (RuntimeException e) {
            throw new RuntimeException("Error al obtener alertas por tarjeta: " + e.getMessage());
        }
    }

    @GetMapping("/alertas/{id}")
    public ResponseEntity<MonitoreoFraude> obtenerAlertaFraudePorId(@PathVariable Integer id) {
        try {
            MonitoreoFraude alerta = monitoreoFraudeService.obtenerAlertaPorId(id)
                    .orElseThrow(() -> new NotFoundException(id.toString(), ENTITY_NAME));
            return ResponseEntity.ok(alerta);
        } catch (NotFoundException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new RuntimeException("Error al obtener la alerta: " + e.getMessage());
        }
    }

    @PutMapping("/alertas/{id}/procesar")
    public ResponseEntity<Map<String, String>> procesarAlertaFraude(
            @PathVariable Integer id,
            @RequestParam @Pattern(regexp = "PRO|REC|APR|REV") String estado,
            @RequestParam(required = false) String detalle,
            @RequestParam(required = false) String accionTomada,
            @RequestParam(required = false) Boolean requiereVerificacion,
            @RequestParam(required = false) String motivoVerificacion) {
        try {
            monitoreoFraudeService.procesarAlerta(id, estado, detalle, accionTomada, requiereVerificacion, motivoVerificacion);
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Alerta procesada exitosamente");
            return ResponseEntity.ok(response);
        } catch (NotFoundException e) {
            throw e;
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Error de validación: " + e.getMessage());
        } catch (RuntimeException e) {
            throw new RuntimeException("Error al procesar la alerta: " + e.getMessage());
        }
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFoundException(NotFoundException e) {
        Map<String, String> response = new HashMap<>();
        response.put("error", e.getMessage());
        return ResponseEntity.status(404).body(response);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException e) {
        Map<String, String> response = new HashMap<>();
        response.put("error", e.getMessage());
        return ResponseEntity.status(400).body(response);
    }
} 