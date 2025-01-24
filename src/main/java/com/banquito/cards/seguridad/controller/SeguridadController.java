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

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        try {
            return ResponseEntity.ok(seguridadService.crearSeguridadBanco(seguridadBanco));
        } catch (RuntimeException e) {
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
        return seguridadService.obtenerSeguridadBancoPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
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
        try {
            return ResponseEntity.ok(seguridadService.actualizarSeguridadMarca(marca, nuevaClave));
        } catch (RuntimeException e) {
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
        try {
            return ResponseEntity.ok(seguridadService.crearSeguridadGateway(seguridadGateway));
        } catch (RuntimeException e) {
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
        try {
            return ResponseEntity.ok(seguridadService.actualizarSeguridadProcesador(id, nuevaClave));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

} 