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

} 