package com.banquito.cards.fraude.controller;

import com.banquito.cards.fraude.model.ReglaFraude;
import com.banquito.cards.fraude.service.ReglaFraudeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reglas-fraude")
public class ReglaFraudeController {

    private final ReglaFraudeService reglaFraudeService;

    public ReglaFraudeController(ReglaFraudeService reglaFraudeService) {
        this.reglaFraudeService = reglaFraudeService;
    }

    @GetMapping
    public ResponseEntity<List<ReglaFraude>> obtenerTodasLasReglas() {
        return ResponseEntity.ok(reglaFraudeService.obtenerTodasLasReglas());
    }

    @GetMapping("/activas")
    public ResponseEntity<List<ReglaFraude>> obtenerReglasActivas() {
        return ResponseEntity.ok(reglaFraudeService.obtenerReglasActivas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReglaFraude> obtenerReglaPorId(@PathVariable Integer id) {
        return reglaFraudeService.obtenerReglaPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ReglaFraude> crearRegla(@RequestBody ReglaFraude regla) {
        try {
            return ResponseEntity.ok(reglaFraudeService.crearRegla(regla));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReglaFraude> actualizarRegla(
            @PathVariable Integer id,
            @RequestBody ReglaFraude regla) {
        try {
            return ResponseEntity.ok(reglaFraudeService.actualizarRegla(id, regla));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivarRegla(@PathVariable Integer id) {
        try {
            reglaFraudeService.desactivarRegla(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
} 