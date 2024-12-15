package com.banquito.cards.seguridad.controller;

import com.banquito.cards.seguridad.model.LogConexion;
import com.banquito.cards.seguridad.service.LogConexionService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/logs-conexion")
public class LogConexionController {

    private final LogConexionService logConexionService;

    public LogConexionController(LogConexionService logConexionService) {
        this.logConexionService = logConexionService;
    }

    @PostMapping
    public ResponseEntity<LogConexion> registrarConexion(
            @RequestParam String marca,
            @RequestParam Integer codBanco,
            @RequestParam String ipOrigen,
            @RequestParam String operacion,
            @RequestParam String resultado) {
        try {
            return ResponseEntity.ok(
                    logConexionService.registrarConexion(marca, codBanco, ipOrigen, operacion, resultado));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/banco/{codBanco}")
    public ResponseEntity<List<LogConexion>> obtenerLogsPorBanco(@PathVariable Integer codBanco) {
        return ResponseEntity.ok(logConexionService.obtenerLogsPorBanco(codBanco));
    }

    @GetMapping("/marca/{marca}")
    public ResponseEntity<List<LogConexion>> obtenerLogsPorMarca(@PathVariable String marca) {
        return ResponseEntity.ok(logConexionService.obtenerLogsPorMarca(marca));
    }

    @GetMapping("/fecha")
    public ResponseEntity<List<LogConexion>> obtenerLogsPorFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        return ResponseEntity.ok(logConexionService.obtenerLogsPorFecha(fechaInicio, fechaFin));
    }

    @GetMapping("/resultado/{resultado}")
    public ResponseEntity<List<LogConexion>> obtenerLogsPorResultado(@PathVariable String resultado) {
        return ResponseEntity.ok(logConexionService.obtenerLogsPorResultado(resultado));
    }

    @GetMapping("/operacion/{operacion}")
    public ResponseEntity<List<LogConexion>> obtenerLogsPorOperacion(@PathVariable String operacion) {
        return ResponseEntity.ok(logConexionService.obtenerLogsPorOperacion(operacion));
    }
} 