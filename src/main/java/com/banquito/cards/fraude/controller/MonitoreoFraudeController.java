package com.banquito.cards.fraude.controller;

import com.banquito.cards.fraude.controller.dto.MonitoreoFraudeDTO;
import com.banquito.cards.fraude.controller.mapper.MonitoreoFraudeMapper;
import com.banquito.cards.fraude.model.MonitoreoFraude;
import com.banquito.cards.fraude.service.MonitoreoFraudeService;
import com.banquito.cards.exception.NotFoundException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Tag(name = "Monitoreo de Fraude", description = "API para la gestión y monitoreo de alertas de fraude")
@RestController
@RequestMapping("/api/v1/monitoreo-fraude")
@Validated
public class MonitoreoFraudeController {

    private static final String ENTITY_NAME = "MonitoreoFraude";
    private final MonitoreoFraudeService monitoreoFraudeService;
    private final MonitoreoFraudeMapper monitoreoFraudeMapper;

    public MonitoreoFraudeController(MonitoreoFraudeService monitoreoFraudeService, 
                                   MonitoreoFraudeMapper monitoreoFraudeMapper) {
        this.monitoreoFraudeService = monitoreoFraudeService;
        this.monitoreoFraudeMapper = monitoreoFraudeMapper;
    }

    @Operation(summary = "Obtener alertas pendientes", 
               description = "Retorna todas las alertas de fraude que están pendientes de revisión")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de alertas pendientes obtenida exitosamente",
                    content = {@Content(schema = @Schema(implementation = MonitoreoFraudeDTO.class))}),
        @ApiResponse(responseCode = "400", description = "Error al obtener las alertas pendientes")
    })
    @GetMapping("/alertas/pendientes")
    public ResponseEntity<?> obtenerAlertasPendientes() {
        try {
            List<MonitoreoFraudeDTO> alertas = monitoreoFraudeService.obtenerAlertasPendientes()
                .stream()
                .map(monitoreoFraudeMapper::toDTO)
                .collect(Collectors.toList());
            return ResponseEntity.ok(alertas);
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Operation(summary = "Obtener alertas por rango de fechas", 
               description = "Retorna las alertas de fraude dentro de un rango de fechas específico")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de alertas obtenida exitosamente"),
        @ApiResponse(responseCode = "400", description = "Error en los parámetros de fecha")
    })
    @GetMapping("/alertas/por-fecha")
    public ResponseEntity<?> obtenerAlertasPorFecha(
            @Parameter(description = "Fecha de inicio de la búsqueda") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @Parameter(description = "Fecha fin de la búsqueda") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        try {
            List<MonitoreoFraudeDTO> alertas = monitoreoFraudeService.obtenerAlertasPorFecha(fechaInicio, fechaFin)
                .stream()
                .map(monitoreoFraudeMapper::toDTO)
                .collect(Collectors.toList());
            return ResponseEntity.ok(alertas);
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Operation(summary = "Obtener alertas por transacción", 
               description = "Retorna todas las alertas de fraude asociadas a una transacción específica")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de alertas obtenida exitosamente",
                    content = {@Content(schema = @Schema(implementation = MonitoreoFraudeDTO.class))}),
        @ApiResponse(responseCode = "400", description = "Error al obtener las alertas")
    })
    @GetMapping("/alertas/por-transaccion/{codTransaccion}")
    public ResponseEntity<?> obtenerAlertasPorTransaccion(
            @Parameter(description = "Código de la transacción a consultar") 
            @PathVariable Integer codTransaccion) {
        try {
            List<MonitoreoFraudeDTO> alertas = monitoreoFraudeService.obtenerAlertasPorTransaccion(codTransaccion)
                .stream()
                .map(monitoreoFraudeMapper::toDTO)
                .collect(Collectors.toList());
            return ResponseEntity.ok(alertas);
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Operation(summary = "Obtener alertas por número de tarjeta", 
               description = "Retorna las alertas de fraude asociadas a una tarjeta específica")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de alertas obtenida exitosamente"),
        @ApiResponse(responseCode = "400", description = "Error en los parámetros de búsqueda")
    })
    @GetMapping("/alertas/por-tarjeta/{numeroTarjeta}")
    public ResponseEntity<List<MonitoreoFraude>> obtenerAlertasFraudePorTarjeta(
            @Parameter(description = "Número de tarjeta a consultar") 
            @PathVariable @NotBlank String numeroTarjeta,
            @Parameter(description = "Fecha de inicio de la búsqueda") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @Parameter(description = "Fecha fin de la búsqueda") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        try {
            List<MonitoreoFraude> alertas = monitoreoFraudeService.obtenerAlertasPorTarjeta(numeroTarjeta, fechaInicio, fechaFin);
            return ResponseEntity.ok(alertas);
        } catch (RuntimeException e) {
            throw new RuntimeException("Error al obtener alertas por tarjeta: " + e.getMessage());
        }
    }

    @Operation(summary = "Obtener alerta por ID", 
               description = "Retorna una alerta de fraude específica basada en su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Alerta encontrada exitosamente",
                    content = {@Content(schema = @Schema(implementation = MonitoreoFraude.class))}),
        @ApiResponse(responseCode = "404", description = "Alerta no encontrada"),
        @ApiResponse(responseCode = "400", description = "Error al obtener la alerta")
    })
    @GetMapping("/alertas/{id}")
    public ResponseEntity<MonitoreoFraude> obtenerAlertaFraudePorId(
            @Parameter(description = "ID de la alerta a consultar") 
            @PathVariable Integer id) {
        try {
            MonitoreoFraude alerta = monitoreoFraudeService.obtenerAlertaPorId(id)
                    .orElseThrow(() -> new NotFoundException(id.toString(), ENTITY_NAME));
            return ResponseEntity.ok(alerta);
        } catch (NotFoundException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new RuntimeException("Error al obtener la alerta: " + e.getMessage());
        }
    }

    @Operation(summary = "Procesar alerta de fraude", 
               description = "Actualiza el estado de una alerta de fraude y registra la decisión tomada")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Alerta procesada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Error en el procesamiento de la alerta"),
        @ApiResponse(responseCode = "404", description = "Alerta no encontrada")
    })
    @PutMapping("/alertas/{id}/procesar")
    public ResponseEntity<?> procesarAlerta(
            @Parameter(description = "ID de la alerta de fraude") 
            @PathVariable Integer id,
            @Parameter(description = "Nuevo estado de la alerta") 
            @RequestParam String estado,
            @Parameter(description = "Detalle o comentarios sobre la decisión") 
            @RequestParam(required = false) String detalle) {
        try {
            monitoreoFraudeService.procesarAlerta(id, estado, detalle != null ? detalle : "Alerta procesada");
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Alerta procesada exitosamente");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFoundException(NotFoundException e) {
        Map<String, String> response = new HashMap<>();
        response.put("error", e.getMessage());
        return ResponseEntity.status(404).body(response);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException e) {
        Map<String, String> response = new HashMap<>();
        response.put("error", e.getMessage());
        return ResponseEntity.status(400).body(response);
    }
} 