package com.banquito.cards.transaccion.controller;

import com.banquito.cards.transaccion.service.HistorialEstadoTransaccionService;
import com.banquito.cards.transaccion.controller.dto.HistorialEstadoTransaccionDTO;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/historial-estados")
public class HistorialEstadoTransaccionController {

    private final HistorialEstadoTransaccionService historialService;

    public HistorialEstadoTransaccionController(HistorialEstadoTransaccionService historialService) {
        this.historialService = historialService;
    }

    @GetMapping("/buscar")
    public ResponseEntity<?> buscarPorFechaYEstado(
            @RequestParam(required = false) String estado,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin,
            @RequestParam(required = false) String bancoNombre) {
        try {
            List<HistorialEstadoTransaccionDTO> historial = historialService.obtenerHistorialPorFechaYEstado(
                estado, fechaInicio, fechaFin, bancoNombre);
            return ResponseEntity.ok(historial);
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/por-fecha")
    public ResponseEntity<?> buscarPorFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fecha) {
        try {
            List<HistorialEstadoTransaccionDTO> historial = historialService.obtenerHistorialPorFecha(fecha);
            return ResponseEntity.ok(historial);
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/registrar")
    public ResponseEntity<?> registrarCambioEstado(
            @RequestParam Integer transaccionId,
            @RequestParam String nuevoEstado,
            @RequestParam(required = false) String detalle) {
        try {
            HistorialEstadoTransaccionDTO historial = historialService.registrarCambioEstado(
                transaccionId, nuevoEstado, detalle != null ? detalle : "Cambio de estado manual");
            return ResponseEntity.ok(historial);
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
} 