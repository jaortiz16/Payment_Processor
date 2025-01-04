package com.banquito.cards.seguridad.controller;

import com.banquito.cards.seguridad.model.*;
import com.banquito.cards.seguridad.service.SeguridadService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/seguridad")
public class SeguridadController {

    private final SeguridadService seguridadService;

    public SeguridadController(SeguridadService seguridadService) {
        this.seguridadService = seguridadService;
    }

    @PostMapping("/bancos")
    public ResponseEntity<SeguridadBanco> crearSeguridadBanco(@RequestBody SeguridadBanco seguridadBanco) {
        try {
            return ResponseEntity.ok(seguridadService.crearSeguridadBanco(seguridadBanco));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/bancos/{id}")
    public ResponseEntity<SeguridadBanco> obtenerSeguridadBanco(@PathVariable Integer id) {
        return seguridadService.obtenerSeguridadBancoPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/marcas/{marca}")
    public ResponseEntity<SeguridadMarca> actualizarSeguridadMarca(
            @PathVariable String marca,
            @RequestParam String nuevaClave) {
        try {
            return ResponseEntity.ok(seguridadService.actualizarSeguridadMarca(marca, nuevaClave));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/gateways")
    public ResponseEntity<SeguridadGateway> crearSeguridadGateway(
            @RequestBody SeguridadGateway seguridadGateway) {
        try {
            return ResponseEntity.ok(seguridadService.crearSeguridadGateway(seguridadGateway));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/procesadores/{id}")
    public ResponseEntity<SeguridadProcesador> actualizarSeguridadProcesador(
            @PathVariable Integer id,
            @RequestParam String nuevaClave) {
        try {
            return ResponseEntity.ok(seguridadService.actualizarSeguridadProcesador(id, nuevaClave));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }


} 