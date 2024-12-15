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
} 