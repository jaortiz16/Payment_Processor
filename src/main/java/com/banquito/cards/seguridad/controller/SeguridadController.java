package com.banquito.cards.seguridad.controller;

import com.banquito.cards.seguridad.model.*;
import com.banquito.cards.seguridad.service.SeguridadService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "Seguridad", description = "API para la gestión de seguridad de bancos, marcas y gateways")
@RestController
@RequestMapping("/api/v1/seguridad")
public class SeguridadController {

    private final SeguridadService seguridadService;

    public SeguridadController(SeguridadService seguridadService) {
        this.seguridadService = seguridadService;
    }

    @Operation(summary = "Crear seguridad de banco", 
               description = "Registra una nueva configuración de seguridad para un banco")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Configuración creada exitosamente",
                    content = {@Content(schema = @Schema(implementation = SeguridadBanco.class))}),
        @ApiResponse(responseCode = "400", description = "Error en los datos de seguridad")
    })
    @PostMapping("/bancos")
    public ResponseEntity<SeguridadBanco> crearSeguridadBanco(
            @Parameter(description = "Datos de seguridad del banco") 
            @RequestBody SeguridadBanco seguridadBanco) {
        log.info("Intentando registrar nueva configuración de seguridad para banco: {}", seguridadBanco);
        try {
            SeguridadBanco createdBanco = seguridadService.crearSeguridadBanco(seguridadBanco);
            log.info("Registro exitoso de configuración de seguridad para banco: {}", createdBanco);
            return ResponseEntity.ok(createdBanco);
        } catch (RuntimeException e) {
            log.error("Error al registrar configuración de seguridad para banco: {}, error: {}", seguridadBanco, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Obtener seguridad de banco", 
               description = "Retorna la configuración de seguridad de un banco específico")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Configuración encontrada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Configuración no encontrada")
    })
    @GetMapping("/bancos/{id}")
    public ResponseEntity<SeguridadBanco> obtenerSeguridadBanco(
            @Parameter(description = "ID del banco") 
            @PathVariable Integer id) {
        log.info("Buscando configuración de seguridad para banco con ID: {}", id);
        return seguridadService.obtenerSeguridadBancoPorId(id)
                .map(banco -> {
                    log.info("Configuración encontrada para banco con ID: {}", id);
                    return ResponseEntity.ok(banco);
                })
                .orElseGet(() -> {
                    log.warn("No se encontró configuración para banco con ID: {}", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @Operation(summary = "Actualizar seguridad de marca", 
               description = "Actualiza la clave de seguridad para una marca específica")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Seguridad actualizada exitosamente",
                    content = {@Content(schema = @Schema(implementation = SeguridadMarca.class))}),
        @ApiResponse(responseCode = "400", description = "Error en la actualización")
    })
    @PutMapping("/marcas/{marca}")
    public ResponseEntity<SeguridadMarca> actualizarSeguridadMarca(
            @Parameter(description = "Nombre de la marca") 
            @PathVariable String marca,
            @Parameter(description = "Nueva clave de seguridad") 
            @RequestParam String nuevaClave) {
        log.info("Intentando actualizar clave de seguridad para marca: {}, nuevaClave: {}", marca, nuevaClave);
        try {
            SeguridadMarca updatedMarca = seguridadService.actualizarSeguridadMarca(marca, nuevaClave);
            log.info("Actualización exitosa de clave de seguridad para marca: {}", updatedMarca);
            return ResponseEntity.ok(updatedMarca);
        } catch (RuntimeException e) {
            log.error("Error al actualizar clave de seguridad para marca: {}, error: {}", marca, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Crear seguridad de gateway", 
               description = "Registra una nueva configuración de seguridad para un gateway")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Configuración creada exitosamente",
                    content = {@Content(schema = @Schema(implementation = SeguridadGateway.class))}),
        @ApiResponse(responseCode = "400", description = "Error en los datos de seguridad")
    })
    @PostMapping("/gateways")
    public ResponseEntity<SeguridadGateway> crearSeguridadGateway(
            @Parameter(description = "Datos de seguridad del gateway") 
            @RequestBody SeguridadGateway seguridadGateway) {
        log.info("Intentando registrar nueva configuración de seguridad para gateway: {}", seguridadGateway);
        try {
            SeguridadGateway createdGateway = seguridadService.crearSeguridadGateway(seguridadGateway);
            log.info("Registro exitoso de configuración de seguridad para gateway: {}", createdGateway);
            return ResponseEntity.ok(createdGateway);
        } catch (RuntimeException e) {
            log.error("Error al registrar configuración de seguridad para gateway: {}, error: {}", seguridadGateway, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Actualizar seguridad de procesador", 
               description = "Actualiza la clave de seguridad para un procesador específico")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Seguridad actualizada exitosamente",
                    content = {@Content(schema = @Schema(implementation = SeguridadProcesador.class))}),
        @ApiResponse(responseCode = "400", description = "Error en la actualización")
    })
    @PutMapping("/procesadores/{id}")
    public ResponseEntity<SeguridadProcesador> actualizarSeguridadProcesador(
            @Parameter(description = "ID del procesador") 
            @PathVariable Integer id,
            @Parameter(description = "Nueva clave de seguridad") 
            @RequestParam String nuevaClave) {
        log.info("Intentando actualizar clave de seguridad para procesador con ID: {}, nuevaClave: {}", id, nuevaClave);
        try {
            SeguridadProcesador updatedProcesador = seguridadService.actualizarSeguridadProcesador(id, nuevaClave);
            log.info("Actualización exitosa de clave de seguridad para procesador: {}", updatedProcesador);
            return ResponseEntity.ok(updatedProcesador);
        } catch (RuntimeException e) {
            log.error("Error al actualizar clave de seguridad para procesador con ID: {}, error: {}", id, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}