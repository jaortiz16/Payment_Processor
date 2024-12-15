package com.banquito.cards.transaccion.controller;

import com.banquito.cards.transaccion.model.HistorialEstadoTransaccion;
import com.banquito.cards.transaccion.service.HistorialEstadoTransaccionService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/historial-estados")
public class HistorialEstadoTransaccionController {

    private final HistorialEstadoTransaccionService historialService;

    public HistorialEstadoTransaccionController(HistorialEstadoTransaccionService historialService) {
        this.historialService = historialService;
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

    @GetMapping("/transacciones/{transaccionId}")
    public ResponseEntity<List<HistorialEstadoTransaccion>> obtenerHistorialPorTransaccion(
            @PathVariable Integer transaccionId) {
        return ResponseEntity.ok(historialService.obtenerHistorialPorTransaccion(transaccionId));
    }

    @GetMapping("/estados/{estado}")
    public ResponseEntity<List<HistorialEstadoTransaccion>> obtenerHistorialPorEstado(
            @PathVariable String estado) {
        return ResponseEntity.ok(historialService.obtenerHistorialPorEstado(estado));
    }

    @GetMapping("/fecha")
    public ResponseEntity<List<HistorialEstadoTransaccion>> obtenerHistorialPorFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        return ResponseEntity.ok(historialService.obtenerHistorialPorFecha(fechaInicio, fechaFin));
    }
} 