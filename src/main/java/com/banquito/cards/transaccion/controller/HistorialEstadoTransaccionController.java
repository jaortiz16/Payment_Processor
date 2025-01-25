package com.banquito.cards.transaccion.controller;

import com.banquito.cards.transaccion.service.HistorialEstadoTransaccionService;
import com.banquito.cards.transaccion.controller.dto.HistorialEstadoTransaccionDTO;
import com.banquito.cards.transaccion.controller.mapper.HistorialEstadoTransaccionMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Tag(name = "Historial de Estados", description = "API para la gestión del historial de estados de las transacciones")
@RestController
@RequestMapping("/api/v1/historial-estados")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class HistorialEstadoTransaccionController {

    private static final Logger log = LoggerFactory.getLogger(HistorialEstadoTransaccionController.class);

    private final HistorialEstadoTransaccionService historialService;
    private final HistorialEstadoTransaccionMapper historialMapper;

    public HistorialEstadoTransaccionController(HistorialEstadoTransaccionService historialService,
                                              HistorialEstadoTransaccionMapper historialMapper) {
        this.historialService = historialService;
        this.historialMapper = historialMapper;
    }

    @Operation(summary = "Obtener historial de transacciones", 
               description = "Retorna el historial de estados de las transacciones según los filtros especificados")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Historial obtenido exitosamente",
                    content = {@Content(schema = @Schema(implementation = HistorialEstadoTransaccionDTO.class))}),
        @ApiResponse(responseCode = "400", description = "Error en los parámetros de búsqueda")
    })
    @GetMapping("/transacciones")
    public ResponseEntity<List<HistorialEstadoTransaccionDTO>> obtenerHistorialTransacciones(
            @Parameter(description = "Estado de la transacción") 
            @RequestParam(required = false) String estado,
            @Parameter(description = "Fecha inicial del rango de búsqueda") 
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @Parameter(description = "Fecha final del rango de búsqueda") 
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin,
            @Parameter(description = "Nombre del banco") 
            @RequestParam(required = false) String bancoNombre) {
        try {
            // Si no se proporcionan fechas, usar el día actual
            if (fechaInicio == null && fechaFin == null) {
                LocalDateTime ahora = LocalDateTime.now();
                fechaInicio = ahora.toLocalDate().atStartOfDay();
                fechaFin = ahora.toLocalDate().atTime(23, 59, 59);
            }

            List<HistorialEstadoTransaccionDTO> historial = historialService.obtenerHistorialPorFechaYEstado(
                estado, fechaInicio, fechaFin, bancoNombre);
            
            return ResponseEntity.ok(historial);
        } catch (RuntimeException e) {
            log.error("Error al obtener historial de transacciones", e);
            throw e;
        }
    }

    @Operation(summary = "Obtener historial por fecha", 
               description = "Retorna el historial de estados de las transacciones para una fecha específica")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Historial obtenido exitosamente"),
        @ApiResponse(responseCode = "400", description = "Error en los parámetros de búsqueda")
    })
    @GetMapping("/transacciones/fecha")
    public ResponseEntity<List<HistorialEstadoTransaccionDTO>> obtenerHistorialPorFecha(
            @Parameter(description = "Fecha de búsqueda") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fecha) {
        try {
            List<HistorialEstadoTransaccionDTO> historial = historialService.obtenerHistorialPorFecha(fecha);
            return ResponseEntity.ok(historial != null ? historial : new ArrayList<>());
        } catch (RuntimeException e) {
            return ResponseEntity.ok(new ArrayList<>());
        }
    }

    @Operation(summary = "Registrar cambio de estado", 
               description = "Registra un nuevo cambio de estado para una transacción")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cambio de estado registrado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Error en los datos del cambio de estado")
    })
    @PostMapping("/transacciones/{transaccionId}/estados")
    public ResponseEntity<Object> registrarCambioEstado(
            @Parameter(description = "ID de la transacción") 
            @PathVariable Integer transaccionId,
            @Parameter(description = "Nuevo estado de la transacción") 
            @RequestParam String nuevoEstado,
            @Parameter(description = "Detalle del cambio de estado") 
            @RequestParam(required = false) String detalle) {
        try {
            if (transaccionId == null || nuevoEstado == null || nuevoEstado.trim().isEmpty()) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "El ID de transacción y el nuevo estado son requeridos");
                return ResponseEntity.badRequest().body(response);
            }

            HistorialEstadoTransaccionDTO historial = historialService.registrarCambioEstado(
                transaccionId, nuevoEstado, detalle != null ? detalle : "");
                
            if (historial == null) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "No se pudo registrar el cambio de estado");
                return ResponseEntity.badRequest().body(response);
            }
            
            return ResponseEntity.ok(historial);
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
} 