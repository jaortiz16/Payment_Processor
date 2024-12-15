package com.banquito.cards.comision.controller;

import com.banquito.cards.comision.model.Banco;
import com.banquito.cards.comision.service.BancoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bancos")
public class BancoController {

    private final BancoService bancoService;

    public BancoController(BancoService bancoService) {
        this.bancoService = bancoService;
    }

    @GetMapping
    public ResponseEntity<List<Banco>> listarTodos() {
        return ResponseEntity.ok(bancoService.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Banco> obtenerPorId(@PathVariable Integer id) {
        return bancoService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Banco> crear(@RequestBody Banco banco) {
        return ResponseEntity.ok(bancoService.crear(banco));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Banco> actualizar(@PathVariable Integer id, @RequestBody Banco banco) {
        try {
            return ResponseEntity.ok(bancoService.actualizar(id, banco));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> inactivar(@PathVariable Integer id) {
        try {
            bancoService.inactivar(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
} 