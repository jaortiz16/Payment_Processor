package com.banquito.cards.comision.controller;

import com.banquito.cards.comision.model.Comision;
import com.banquito.cards.comision.model.ComisionSegmento;
import com.banquito.cards.comision.service.ComisionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/comisiones")
public class ComisionController {

    private final ComisionService comisionService;

    public ComisionController(ComisionService comisionService) {
        this.comisionService = comisionService;
    }

    @GetMapping("/tipo-comision/{tipo}")
    public ResponseEntity<List<Comision>> obtenerPorTipo(@PathVariable String tipo) {
        try {
            return ResponseEntity.ok(comisionService.obtenerComisionesPorTipo(tipo));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/monto-comision")
    public ResponseEntity<List<Comision>> buscarPorMontoBase(
            @RequestParam BigDecimal montoMinimo,
            @RequestParam BigDecimal montoMaximo) {
        try {
            return ResponseEntity.ok(
                comisionService.obtenerComisionesPorMontoBaseEntre(montoMinimo, montoMaximo));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/obtener-por-id/{id}")
    public ResponseEntity<Comision> obtenerPorId(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(comisionService.obtenerComisionPorId(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/crear-comision")
    public ResponseEntity<Comision> crearComision(@RequestBody Comision comision) {
        try {
            return ResponseEntity.ok(comisionService.crearComision(comision));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/actualizar-comision/{id}")
    public ResponseEntity<Comision> actualizarComision(@PathVariable Integer id, @RequestBody Comision comision) {
        try {
            return ResponseEntity.ok(comisionService.actualizarComision(id, comision));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
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

    @GetMapping("/{id}/calcular")
    public ResponseEntity<BigDecimal> calcularComision(
            @PathVariable Integer id,
            @RequestParam Integer numeroTransacciones,
            @RequestParam BigDecimal montoTransaccion) {
        try {
            return ResponseEntity.ok(
                comisionService.calcularComision(id, numeroTransacciones, montoTransaccion));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}