package com.banquito.cards.transaccion.controller;

import com.banquito.cards.transaccion.model.Transaccion;
import com.banquito.cards.transaccion.model.HistorialEstadoTransaccion;
import com.banquito.cards.transaccion.service.TransaccionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transacciones")
public class TransaccionController {

    private final TransaccionService transaccionService;

    public TransaccionController(TransaccionService transaccionService) {
        this.transaccionService = transaccionService;
    }

    @GetMapping
    public ResponseEntity<List<Transaccion>> listarTransacciones() {
        return ResponseEntity.ok(transaccionService.obtenerTodasLasTransacciones());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transaccion> obtenerTransaccion(@PathVariable Integer id) {
        return transaccionService.obtenerTransaccionPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Transaccion> crearTransaccion(@RequestBody Transaccion transaccion) {
        return ResponseEntity.ok(transaccionService.crearTransaccion(transaccion));
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<Transaccion> actualizarEstado(
            @PathVariable Integer id,
            @RequestParam String nuevoEstado,
            @RequestParam(required = false) String detalle) {
        try {
            return ResponseEntity.ok(
                    transaccionService.actualizarEstadoTransaccion(id, nuevoEstado, detalle));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
} 