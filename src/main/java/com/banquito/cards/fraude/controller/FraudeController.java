package com.banquito.cards.fraude.controller;

import com.banquito.cards.fraude.model.MonitoreoFraude;
import com.banquito.cards.fraude.model.ReglaFraude;
import com.banquito.cards.fraude.service.FraudeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/fraude")
public class FraudeController {

    private final FraudeService fraudeService;

    public FraudeController(FraudeService fraudeService) {
        this.fraudeService = fraudeService;
    }

    @GetMapping("/reglas")
    public ResponseEntity<List<ReglaFraude>> listarReglas() {
        return ResponseEntity.ok(fraudeService.obtenerTodasLasReglas());
    }

    @GetMapping("/reglas/{id}")
    public ResponseEntity<ReglaFraude> obtenerReglaPorId(@PathVariable Integer id) {
        return fraudeService.obtenerReglaPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/reglas")
    public ResponseEntity<ReglaFraude> crearRegla(@RequestBody ReglaFraude regla) {
        return ResponseEntity.ok(fraudeService.crearRegla(regla));
    }

    @PutMapping("/reglas/{id}")
    public ResponseEntity<ReglaFraude> actualizarRegla(
            @PathVariable Integer id,
            @RequestBody ReglaFraude regla) {
        try {
            return ResponseEntity.ok(fraudeService.actualizarRegla(id, regla));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/reglas/{id}/monitoreos")
    public ResponseEntity<MonitoreoFraude> registrarMonitoreo(
            @PathVariable Integer id,
            @RequestParam String riesgo) {
        try {
            return ResponseEntity.ok(fraudeService.registrarMonitoreo(id, riesgo));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
} 