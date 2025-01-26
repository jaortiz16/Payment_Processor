package com.banquito.cards.fraude.controller;

import com.banquito.cards.fraude.controller.dto.MonitoreoFraudeDTO;
import com.banquito.cards.fraude.controller.dto.FraudeResponseDTO;
import com.banquito.cards.fraude.controller.mapper.MonitoreoFraudeMapper;
import com.banquito.cards.fraude.model.MonitoreoFraude;
import com.banquito.cards.fraude.service.MonitoreoFraudeService;
import com.banquito.cards.exception.NotFoundException;
import com.banquito.cards.exception.BusinessException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Tag(name = "Monitoreo de Fraude", description = "API para la gestión y monitoreo de alertas de fraude")
@RestController
@RequestMapping("/v1/fraudes/monitoreo")
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

    @Operation(summary = "Listar alertas de fraude", description = "Retorna todas las alertas de fraude con paginación, filtrado y ordenamiento")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de alertas obtenida exitosamente",
                    content = {@Content(schema = @Schema(implementation = MonitoreoFraudeDTO.class))}),
        @ApiResponse(responseCode = "400", description = "Parámetros de búsqueda inválidos")
    })
    @GetMapping
    public ResponseEntity<Page<MonitoreoFraudeDTO>> listarAlertas(
            @Parameter(description = "Número de página (0..N)") 
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de la página") 
            @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Campo por el cual ordenar") 
            @RequestParam(defaultValue = "fechaDeteccion") String sortBy,
            @Parameter(description = "Dirección del ordenamiento (asc o desc)") 
            @RequestParam(defaultValue = "desc") String sortDir,
            @Parameter(description = "Estado de la alerta (PEN, PRO, REC, APR, REV)") 
            @RequestParam(required = false) String estado,
            @Parameter(description = "Nivel de riesgo (BAJ, MED, ALT)") 
            @RequestParam(required = false) String nivelRiesgo) {
        try {
            Sort.Direction direction = Sort.Direction.fromString(sortDir.toUpperCase());
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
            
            Page<MonitoreoFraude> alertas = monitoreoFraudeService.obtenerAlertas(pageable, estado, nivelRiesgo);
            Page<MonitoreoFraudeDTO> alertasDTO = alertas.map(monitoreoFraudeMapper::toDTO);
            
            return ResponseEntity.ok(alertasDTO);
        } catch (RuntimeException e) {
            throw new BusinessException("listar alertas", ENTITY_NAME, e.getMessage());
        }
    }

    @Operation(summary = "Obtener alerta por ID", description = "Retorna una alerta específica basada en su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Alerta encontrada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Alerta no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<MonitoreoFraudeDTO> obtenerAlerta(
            @Parameter(description = "ID de la alerta") 
            @PathVariable String id) {
        try {
            MonitoreoFraude alerta = monitoreoFraudeService.obtenerAlertaPorId(id);
            return ResponseEntity.ok(monitoreoFraudeMapper.toDTO(alerta));
        } catch (RuntimeException e) {
            throw new NotFoundException(id, ENTITY_NAME);
        }
    }

    @Operation(summary = "Obtener alertas por transacción", description = "Retorna las alertas asociadas a una transacción específica")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de alertas obtenida exitosamente"),
        @ApiResponse(responseCode = "400", description = "Error en los parámetros de búsqueda")
    })
    @GetMapping("/transacciones/{codTransaccion}")
    public ResponseEntity<List<MonitoreoFraudeDTO>> obtenerAlertasPorTransaccion(
            @Parameter(description = "Código de la transacción") 
            @PathVariable Integer codTransaccion) {
        try {
            List<MonitoreoFraude> alertas = monitoreoFraudeService.obtenerAlertasPorTransaccion(codTransaccion);
            List<MonitoreoFraudeDTO> alertasDTO = alertas.stream()
                    .map(monitoreoFraudeMapper::toDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(alertasDTO);
        } catch (RuntimeException e) {
            throw new BusinessException(codTransaccion.toString(), ENTITY_NAME, "buscar por transacción");
        }
    }

    @Operation(summary = "Obtener alertas por tarjeta", description = "Retorna las alertas asociadas a una tarjeta en un rango de fechas")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de alertas obtenida exitosamente"),
        @ApiResponse(responseCode = "400", description = "Error en los parámetros de búsqueda")
    })
    @GetMapping("/tarjetas/{numeroTarjeta}")
    public ResponseEntity<List<MonitoreoFraudeDTO>> obtenerAlertasPorTarjeta(
            @Parameter(description = "Número de tarjeta") 
            @PathVariable @NotBlank String numeroTarjeta,
            @Parameter(description = "Fecha de inicio") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @Parameter(description = "Fecha fin") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        try {
            List<MonitoreoFraude> alertas = monitoreoFraudeService.obtenerAlertasPorTarjeta(numeroTarjeta, fechaInicio, fechaFin);
            List<MonitoreoFraudeDTO> alertasDTO = alertas.stream()
                    .map(monitoreoFraudeMapper::toDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(alertasDTO);
        } catch (RuntimeException e) {
            throw new BusinessException(numeroTarjeta, ENTITY_NAME, "buscar por tarjeta");
        }
    }

    @Operation(summary = "Actualizar estado de alerta", description = "Actualiza el estado de una alerta de fraude")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Estado actualizado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Alerta no encontrada")
    })
    @PatchMapping("/{id}/estado")
    public ResponseEntity<MonitoreoFraudeDTO> actualizarEstadoAlerta(
            @Parameter(description = "ID de la alerta") 
            @PathVariable String id,
            @Parameter(description = "Nuevo estado") 
            @RequestParam @Pattern(regexp = "PEN|PRO|REC|APR|REV") String estado) {
        try {
            MonitoreoFraude alerta = monitoreoFraudeService.actualizarEstadoAlerta(id, estado);
            return ResponseEntity.ok(monitoreoFraudeMapper.toDTO(alerta));
        } catch (RuntimeException e) {
            throw new BusinessException(id, ENTITY_NAME, "actualizar estado");
        }
    }

    @ExceptionHandler({NotFoundException.class})
    public ResponseEntity<FraudeResponseDTO> handleNotFoundException(NotFoundException e) {
        return ResponseEntity.status(404).body(new FraudeResponseDTO(e.getMessage(), true));
    }

    @ExceptionHandler({BusinessException.class})
    public ResponseEntity<FraudeResponseDTO> handleBusinessException(BusinessException e) {
        return ResponseEntity.status(400).body(new FraudeResponseDTO(e.getMessage(), true));
    }
} 