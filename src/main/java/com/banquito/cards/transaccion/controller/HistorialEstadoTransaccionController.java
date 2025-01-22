package com.banquito.cards.transaccion.controller;

import com.banquito.cards.transaccion.service.HistorialEstadoTransaccionService;
import com.banquito.cards.transaccion.controller.dto.HistorialEstadoTransaccionDTO;
import com.banquito.cards.transaccion.controller.mapper.HistorialEstadoTransaccionMapper;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/historial-estados")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class HistorialEstadoTransaccionController {

    private static final Logger log = LoggerFactory.getLogger(HistorialEstadoTransaccionController.class);

    private final HistorialEstadoTransaccionService historialService;
    private final HistorialEstadoTransaccionMapper historialMapper;

    public HistorialEstadoTransaccionController(HistorialEstadoTransaccionService historialService,
                                              HistorialEstadoTransaccionMapper historialMapper) {
        this.historialService = historialService;
        this.historialMapper = historialMapper;
    }

    @GetMapping("/transacciones")
    public ResponseEntity<List<HistorialEstadoTransaccionDTO>> obtenerHistorialTransacciones(
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

            List<HistorialEstadoTransaccionDTO> historial = historialService.obtenerHistorialPorFechaYEstado(
                estado, fechaInicio, fechaFin, bancoNombre);
            
            return ResponseEntity.ok(historial);
        } catch (RuntimeException e) {
            log.error("Error al obtener historial de transacciones", e);
            throw e;
        }
    }

    @GetMapping("/transacciones/fecha")
    public ResponseEntity<List<HistorialEstadoTransaccionDTO>> obtenerHistorialPorFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fecha) {
        try {
            List<HistorialEstadoTransaccionDTO> historial = historialService.obtenerHistorialPorFecha(fecha);
            return ResponseEntity.ok(historial != null ? historial : new ArrayList<>());
        } catch (RuntimeException e) {
            return ResponseEntity.ok(new ArrayList<>());
        }
    }

    @PostMapping("/transacciones/{transaccionId}/estados")
    public ResponseEntity<Object> registrarCambioEstado(
            @PathVariable Integer transaccionId,
            @RequestParam String nuevoEstado,
            @RequestParam(required = false) String detalle) {
        try {
            if (transaccionId == null || nuevoEstado == null || nuevoEstado.trim().isEmpty()) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "El ID de transacción y el nuevo estado son requeridos");
                return ResponseEntity.badRequest().body(response);
            }

            HistorialEstadoTransaccionDTO historial = historialService.registrarCambioEstado(
                transaccionId, nuevoEstado, detalle != null ? detalle : "");
                
            if (historial == null) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "No se pudo registrar el cambio de estado");
                return ResponseEntity.badRequest().body(response);
            }
            
            return ResponseEntity.ok(historial);
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
} 