package com.banquito.cards.fraude.controller;

import com.banquito.cards.fraude.controller.dto.MonitoreoFraudeDTO;
import com.banquito.cards.fraude.controller.mapper.MonitoreoFraudeMapper;
import com.banquito.cards.fraude.model.MonitoreoFraude;
import com.banquito.cards.fraude.service.MonitoreoFraudeService;
import com.banquito.cards.exception.NotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/v1/monitoreo-fraude")
@Validated
public class MonitoreoFraudeController {

    private static final String ENTITY_NAME = "MonitoreoFraude";
    private final MonitoreoFraudeService monitoreoFraudeService;
    private final MonitoreoFraudeMapper monitoreoFraudeMapper;

    public MonitoreoFraudeController(MonitoreoFraudeService monitoreoFraudeService, 
                                   MonitoreoFraudeMapper monitoreoFraudeMapper) {
        this.monitoreoFraudeService = monitoreoFraudeService;
        this.monitoreoFraudeMapper = monitoreoFraudeMapper;
    }

    @GetMapping("/alertas/pendientes")
    public ResponseEntity<?> obtenerAlertasPendientes() {
        try {
            List<MonitoreoFraudeDTO> alertas = monitoreoFraudeService.obtenerAlertasPendientes()
                .stream()
                .map(monitoreoFraudeMapper::toDTO)
                .collect(Collectors.toList());
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
            List<MonitoreoFraudeDTO> alertas = monitoreoFraudeService.obtenerAlertasPorFecha(fechaInicio, fechaFin)
                .stream()
                .map(monitoreoFraudeMapper::toDTO)
                .collect(Collectors.toList());
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
            List<MonitoreoFraudeDTO> alertas = monitoreoFraudeService.obtenerAlertasPorTransaccion(codTransaccion)
                .stream()
                .map(monitoreoFraudeMapper::toDTO)
                .collect(Collectors.toList());
            return ResponseEntity.ok(alertas);
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
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