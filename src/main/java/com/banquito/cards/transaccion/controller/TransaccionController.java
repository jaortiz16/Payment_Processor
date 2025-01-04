package com.banquito.cards.transaccion.controller;

import com.banquito.cards.transaccion.model.Transaccion;
import com.banquito.cards.transaccion.model.HistorialEstadoTransaccion;
import com.banquito.cards.transaccion.service.TransaccionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/transacciones")
public class TransaccionController {

    private final TransaccionService transaccionService;

    public TransaccionController(TransaccionService transaccionService) {
        this.transaccionService = transaccionService;
    }


    @GetMapping("/{id}")
    public ResponseEntity<Transaccion> obtenerTransaccion(@PathVariable Integer id) {
        try {
            Transaccion transaccion = transaccionService.obtenerTransaccionPorId(id);
            return ResponseEntity.ok(transaccion);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Transaccion> crearTransaccion(@RequestBody Transaccion transaccion) {
        try {
            return ResponseEntity.ok(transaccionService.crearTransaccion(transaccion));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<Transaccion> actualizarEstado(
            @PathVariable Integer id,
            @RequestParam String nuevoEstado,
            @RequestParam(required = false) String detalle) {
        try {
            return ResponseEntity.ok(
                    transaccionService.actualizarEstadoTransaccion(id, nuevoEstado, 
                        detalle != null ? detalle : "Cambio de estado manual"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/buscar-por-estado-fecha")
    public ResponseEntity<List<Transaccion>> buscarPorEstadoYFecha(
            @RequestParam String estado,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        try {
            return ResponseEntity.ok(
                transaccionService.obtenerTransaccionesPorEstadoYFecha(estado, fechaInicio, fechaFin));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/buscar-por-banco-monto")
    public ResponseEntity<List<Transaccion>> buscarPorBancoYMonto(
            @RequestParam Integer codigoBanco,
            @RequestParam(required = false) BigDecimal montoMinimo,
            @RequestParam(required = false) BigDecimal montoMaximo) {
        try {
            return ResponseEntity.ok(
                transaccionService.obtenerTransaccionesPorBancoYMonto(codigoBanco, montoMinimo, montoMaximo));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}