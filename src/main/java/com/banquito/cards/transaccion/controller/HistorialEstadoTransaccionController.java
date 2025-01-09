package com.banquito.cards.transaccion.controller;

import com.banquito.cards.transaccion.model.HistorialEstadoTransaccion;
import com.banquito.cards.transaccion.service.HistorialEstadoTransaccionService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/v1/historial-estados")
public class HistorialEstadoTransaccionController {

    private final HistorialEstadoTransaccionService historialService;

    public HistorialEstadoTransaccionController(HistorialEstadoTransaccionService historialService) {
        this.historialService = historialService;
    }

    @GetMapping("/transacciones")
    public ResponseEntity<?> obtenerHistorialTransacciones(
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin,
            @RequestParam(required = false) String bancoNombre) {
        try {
            // Si no se proporcionan fechas, usar el día actual
            if (fechaInicio == null && fechaFin == null) {
                LocalDateTime ahora = LocalDateTime.now();
                fechaInicio = ahora.toLocalDate().atStartOfDay();
                fechaFin = ahora.toLocalDate().atTime(23, 59, 59);
            }

            List<HistorialEstadoTransaccion> historial = historialService.obtenerHistorialPorFechaYEstado(
                estado, fechaInicio, fechaFin, bancoNombre);
            return ResponseEntity.ok(historial);
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/transacciones/fecha")
    public ResponseEntity<?> obtenerHistorialPorFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fecha) {
        try {
            List<HistorialEstadoTransaccion> historial = historialService.obtenerHistorialPorFecha(fecha);
            return ResponseEntity.ok(historial);
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/transacciones/{transaccionId}/estados")
    public ResponseEntity<HistorialEstadoTransaccion> registrarCambioEstado(
            @PathVariable Integer transaccionId,
            @RequestParam String nuevoEstado,
            @RequestParam(required = false) String detalle) {
        try {
            return ResponseEntity.ok(
                    historialService.registrarCambioEstado(transaccionId, nuevoEstado, detalle));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
} 