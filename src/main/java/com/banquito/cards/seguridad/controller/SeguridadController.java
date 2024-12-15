package com.banquito.cards.seguridad.controller;

import com.banquito.cards.seguridad.model.*;
import com.banquito.cards.seguridad.service.SeguridadService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/seguridad")
public class SeguridadController {

    private final SeguridadService seguridadService;

    public SeguridadController(SeguridadService seguridadService) {
        this.seguridadService = seguridadService;
    }

    // Endpoints para SeguridadBanco
    @PostMapping("/bancos")
    public ResponseEntity<SeguridadBanco> crearSeguridadBanco(@RequestBody SeguridadBanco seguridadBanco) {
        return ResponseEntity.ok(seguridadService.crearSeguridadBanco(seguridadBanco));
    }

    @GetMapping("/bancos/{id}")
    public ResponseEntity<SeguridadBanco> obtenerSeguridadBanco(@PathVariable Integer id) {
        return seguridadService.obtenerSeguridadBancoPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Endpoints para SeguridadMarca
    @PutMapping("/marcas/{marca}")
    public ResponseEntity<SeguridadMarca> actualizarSeguridadMarca(
            @PathVariable String marca,
            @RequestParam String nuevaClave) {
        return ResponseEntity.ok(seguridadService.actualizarSeguridadMarca(marca, nuevaClave));
    }

    // Endpoints para SeguridadGateway
    @PostMapping("/gateways")
    public ResponseEntity<SeguridadGateway> crearSeguridadGateway(
            @RequestBody SeguridadGateway seguridadGateway) {
        return ResponseEntity.ok(seguridadService.crearSeguridadGateway(seguridadGateway));
    }

    // Endpoints para SeguridadProcesador
    @PutMapping("/procesadores/{id}")
    public ResponseEntity<SeguridadProcesador> actualizarSeguridadProcesador(
            @PathVariable Integer id,
            @RequestParam String nuevaClave) {
        try {
            return ResponseEntity.ok(
                    seguridadService.actualizarSeguridadProcesador(id, nuevaClave));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Endpoints para LogConexion
    @PostMapping("/logs")
    public ResponseEntity<LogConexion> registrarConexion(
            @RequestParam String marca,
            @RequestParam Integer codBanco,
            @RequestParam String ipOrigen,
            @RequestParam String operacion,
            @RequestParam String resultado) {
        try {
            return ResponseEntity.ok(
                    seguridadService.registrarConexion(marca, codBanco, ipOrigen, operacion, resultado));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
} 