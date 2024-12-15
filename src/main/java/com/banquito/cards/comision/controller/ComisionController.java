package com.banquito.cards.comision.controller;

import com.banquito.cards.comision.model.Comision;
import com.banquito.cards.comision.model.ComisionSegmento;
import com.banquito.cards.comision.service.ComisionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/comisiones")
public class ComisionController {

    private final ComisionService comisionService;

    public ComisionController(ComisionService comisionService) {
        this.comisionService = comisionService;
    }

    @GetMapping
    public ResponseEntity<List<Comision>> listarTodas() {
        return ResponseEntity.ok(comisionService.obtenerTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Comision> obtenerPorId(@PathVariable Integer id) {
        return comisionService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Comision> crear(@RequestBody Comision comision) {
        return ResponseEntity.ok(comisionService.crear(comision));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Comision> actualizar(@PathVariable Integer id, @RequestBody Comision comision) {
        try {
            return ResponseEntity.ok(comisionService.actualizar(id, comision));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/segmentos")
    public ResponseEntity<List<ComisionSegmento>> obtenerSegmentos(@PathVariable String id) {
        return ResponseEntity.ok(comisionService.obtenerSegmentosPorComision(id));
    }

    @PostMapping("/{id}/segmentos")
    public ResponseEntity<ComisionSegmento> agregarSegmento(
            @PathVariable Integer id,
            @RequestBody ComisionSegmento segmento) {
        try {
            return ResponseEntity.ok(comisionService.agregarSegmento(id, segmento));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
} 