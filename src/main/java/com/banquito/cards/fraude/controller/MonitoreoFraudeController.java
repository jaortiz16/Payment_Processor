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
            log.info("Obteniendo lista de alertas pendientes");
            List<MonitoreoFraudeDTO> alertas = monitoreoFraudeService.obtenerAlertasPendientes()
                .stream()
                .map(monitoreoFraudeMapper::toDTO)
                .collect(Collectors.toList());
            log.info("Se encontraron {} alertas pendientes", alertas.size());
            return ResponseEntity.ok(alertas);
        } catch (RuntimeException e) {
            log.error("Error al obtener alertas pendientes: {}", e.getMessage());
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
            log.info("Buscando alertas entre {} y {}", fechaInicio, fechaFin);
            List<MonitoreoFraudeDTO> alertas = monitoreoFraudeService.obtenerAlertasPorFecha(fechaInicio, fechaFin)
                .stream()
                .map(monitoreoFraudeMapper::toDTO)
                .collect(Collectors.toList());
            log.info("Se encontraron {} alertas en el rango de fechas especificado", alertas.size());
            return ResponseEntity.ok(alertas);
        } catch (RuntimeException e) {
            log.error("Error al buscar alertas por fecha: {}", e.getMessage());
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
            log.info("Buscando alertas para la transacción: {}", codTransaccion);
            List<MonitoreoFraudeDTO> alertas = monitoreoFraudeService.obtenerAlertasPorTransaccion(codTransaccion)
                .stream()
                .map(monitoreoFraudeMapper::toDTO)
                .collect(Collectors.toList());
            log.info("Se encontraron {} alertas para la transacción {}", alertas.size(), codTransaccion);
            return ResponseEntity.ok(alertas);
        } catch (RuntimeException e) {
            log.error("Error al buscar alertas para la transacción {}: {}", codTransaccion, e.getMessage());
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
            log.info("Buscando alertas para la tarjeta: {} entre {} y {}", 
                    numeroTarjeta, fechaInicio, fechaFin);
            List<MonitoreoFraude> alertas = monitoreoFraudeService.obtenerAlertasPorTarjeta(
                    numeroTarjeta, fechaInicio, fechaFin);
            log.info("Se encontraron {} alertas para la tarjeta {}", alertas.size(), numeroTarjeta);
            return ResponseEntity.ok(alertas);
        } catch (RuntimeException e) {
            log.error("Error al buscar alertas para la tarjeta {}: {}", numeroTarjeta, e.getMessage());
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
            log.info("Buscando alerta por ID: {}", id);
            MonitoreoFraude alerta = monitoreoFraudeService.obtenerAlertaPorId(id)
                    .orElseThrow(() -> {
                        log.error("No se encontró alerta con ID: {}", id);
                        return new NotFoundException(id.toString(), ENTITY_NAME);
                    });
            log.info("Alerta encontrada con ID: {}", id);
            return ResponseEntity.ok(alerta);
        } catch (NotFoundException e) {
            log.error("Alerta no encontrada con ID {}: {}", id, e.getMessage());
            throw e;
        } catch (RuntimeException e) {
            log.error("Error al buscar alerta con ID {}: {}", id, e.getMessage());
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
            log.info("Procesando alerta ID: {} con estado: {}", id, estado);
            monitoreoFraudeService.procesarAlerta(id, estado, detalle != null ? detalle : "Alerta procesada");
            log.info("Alerta ID: {} procesada exitosamente con estado: {}", id, estado);
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Alerta procesada exitosamente");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Error al procesar alerta ID {}: {}", id, e.getMessage());
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFoundException(NotFoundException e) {
        log.error("Entidad no encontrada: {}", e.getMessage());
        Map<String, String> response = new HashMap<>();
        response.put("error", e.getMessage());
        return ResponseEntity.status(404).body(response);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException e) {
        log.error("Error en la aplicación: {}", e.getMessage());
        Map<String, String> response = new HashMap<>();
        response.put("error", e.getMessage());
        return ResponseEntity.status(400).body(response);
    }
} 