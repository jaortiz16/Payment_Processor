package com.banquito.cards.transaccion.controller;

import com.banquito.cards.transaccion.model.Transaccion;
import com.banquito.cards.transaccion.model.HistorialEstadoTransaccion;
import com.banquito.cards.transaccion.repository.HistorialEstadoTransaccionRepository;
import com.banquito.cards.transaccion.service.TransaccionService;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1/transacciones")
public class TransaccionController {

    private final TransaccionService transaccionService;
    private final HistorialEstadoTransaccionRepository historialRepository;

    public TransaccionController(TransaccionService transaccionService,
                               HistorialEstadoTransaccionRepository historialRepository) {
        this.transaccionService = transaccionService;
        this.historialRepository = historialRepository;
    }
        @GetMapping("/{id}")
    public ResponseEntity<?> obtenerTransaccion(@PathVariable Integer id) {
        try {
            Transaccion transaccion = transaccionService.obtenerTransaccionPorId(id);
            return ResponseEntity.ok(transaccion);
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.status(404).body(response);
        }
    }
    @PutMapping("/{id}/estado")
    public ResponseEntity<?> actualizarEstado(
            @PathVariable Integer id,
            @RequestParam String nuevoEstado,
            @RequestParam(required = false) String detalle) {
        try {
            return ResponseEntity.ok(
                    transaccionService.actualizarEstadoTransaccion(id, nuevoEstado, 
                        detalle != null ? detalle : "Cambio de estado manual"));
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/buscar-por-estado-fecha")
    public ResponseEntity<?> buscarPorEstadoYFecha(
            @RequestParam String estado,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        try {
            return ResponseEntity.ok(
                transaccionService.obtenerTransaccionesPorEstadoYFecha(estado, fechaInicio, fechaFin));
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/buscar-por-banco-monto")
    public ResponseEntity<?> buscarPorBancoYMonto(
            @RequestParam Integer codigoBanco,
            @RequestParam(required = false) BigDecimal montoMinimo,
            @RequestParam(required = false) BigDecimal montoMaximo) {
        try {
            return ResponseEntity.ok(
                transaccionService.obtenerTransaccionesPorBancoYMonto(codigoBanco, montoMinimo, montoMaximo));
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping
    public ResponseEntity<?> crearTransaccion(@RequestBody Transaccion transaccion) {
        try {
            // 1. Guardamos la transacción como pendiente
            transaccion.setEstado("PEN");
            transaccion.setFechaCreacion(LocalDateTime.now());
            Transaccion transaccionGuardada = transaccionService.guardarTransaccion(transaccion);

            // 2. Procesamos con el banco y esperamos su respuesta
            try {
                transaccionService.procesarConBanco(transaccionGuardada);
                
                // 3. Obtenemos el estado final de la transacción y su último historial
                Transaccion transaccionFinal = transaccionService.obtenerTransaccionPorId(transaccionGuardada.getCodigo());
                List<HistorialEstadoTransaccion> historiales = historialRepository.findByTransaccionOrderByFechaEstadoCambioDesc(transaccionFinal);
                String detalle = !historiales.isEmpty() ? historiales.get(0).getDetalle() : null;
                
                Map<String, String> response = new HashMap<>();
                
                switch (transaccionFinal.getEstado()) {
                    case "APR":
                        response.put("mensaje", "Transacción aceptada");
                        return ResponseEntity.status(201).body(response);
                    case "REC":
                        response.put("mensaje", "Transacción rechazada");
                        if (detalle != null) {
                            response.put("detalle", detalle);
                        }
                        return ResponseEntity.status(400).body(response);
                    default:
                        response.put("mensaje", "Estado de transacción desconocido");
                        return ResponseEntity.status(400).body(response);
                }
            } catch (Exception e) {
                Map<String, String> response = new HashMap<>();
                response.put("error", e.getMessage());
                return ResponseEntity.status(400).body(response);
            }
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.status(400).body(response);
        }
    }

    @PostMapping("/{codigoUnicoTransaccion}/fraude")
    public ResponseEntity<Map<String, String>> procesarRespuestaFraude(
            @PathVariable String codigoUnicoTransaccion,
            @RequestParam String decision) {
        Map<String, String> response = new HashMap<>();
        Transaccion result = transaccionService.procesarRespuestaFraude(codigoUnicoTransaccion, decision);
        String mensaje;
        
        switch (result.getEstado()) {
            case "APR":
                mensaje = "Transacción aceptada";
                break;
            case "REC":
                mensaje = "Transacción rechazada";
                break;
            default:
                mensaje = "Estado de transacción desconocido";
        }
        
        response.put("mensaje", mensaje);
        return ResponseEntity.ok(response);
    }
}