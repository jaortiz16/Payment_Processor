package com.banquito.cards.transaccion.controller;

import com.banquito.cards.transaccion.service.HistorialEstadoTransaccionService;
import com.banquito.cards.transaccion.controller.dto.HistorialEstadoTransaccionDTO;
import com.banquito.cards.transaccion.controller.dto.TransaccionResponseDTO;
import com.banquito.cards.exception.NotFoundException;
import com.banquito.cards.exception.BusinessException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "Historial de Estados de Transacciones", description = "API para la gestión del historial de estados de transacciones")
@RestController
@RequestMapping("/v1/historial-estados")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class HistorialEstadoTransaccionController {

    private static final String ENTITY_NAME = "HistorialEstadoTransaccion";
    private final HistorialEstadoTransaccionService service;

    public HistorialEstadoTransaccionController(HistorialEstadoTransaccionService service) {
        this.service = service;
    }

    @Operation(summary = "Listar historial de estados", 
               description = "Retorna una lista paginada del historial de estados de transacciones")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado obtenido exitosamente",
                    content = {@Content(schema = @Schema(implementation = HistorialEstadoTransaccionDTO.class))}),
        @ApiResponse(responseCode = "400", description = "Error en los parámetros de búsqueda")
    })
    @GetMapping
    public ResponseEntity<Page<HistorialEstadoTransaccionDTO>> listarHistorial(
            @Parameter(description = "Estado de la transacción") 
            @RequestParam(required = false) String estado,
            @Parameter(description = "Fecha inicial del rango de búsqueda") 
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @Parameter(description = "Fecha final del rango de búsqueda") 
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin,
            @PageableDefault(size = 10) Pageable pageable) {
        try {
            return ResponseEntity.ok(service.listarHistorial(estado, fechaInicio, fechaFin, pageable));
        } catch (RuntimeException e) {
            throw new BusinessException("parámetros de búsqueda", ENTITY_NAME, "listar historial");
        }
    }

    @Operation(summary = "Obtener historial por transacción", 
               description = "Retorna el historial de estados de una transacción específica")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Historial obtenido exitosamente",
                    content = {@Content(schema = @Schema(implementation = HistorialEstadoTransaccionDTO.class))}),
        @ApiResponse(responseCode = "404", description = "Transacción no encontrada")
    })
    @GetMapping("/transaccion/{codigoTransaccion}")
    public ResponseEntity<Page<HistorialEstadoTransaccionDTO>> obtenerHistorialPorTransaccion(
            @Parameter(description = "Código de la transacción") 
            @PathVariable Integer codigoTransaccion,
            @Parameter(description = "Estado específico a filtrar") 
            @RequestParam(required = false) String estado,
            @PageableDefault(size = 10) Pageable pageable) {
        try {
            return ResponseEntity.ok(service.obtenerHistorialPorTransaccion(codigoTransaccion, estado, pageable));
        } catch (RuntimeException e) {
            throw new NotFoundException(codigoTransaccion.toString(), ENTITY_NAME);
        }
    }

    @Operation(summary = "Obtener estado por ID", 
               description = "Retorna un estado específico del historial")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Estado encontrado",
                    content = {@Content(schema = @Schema(implementation = HistorialEstadoTransaccionDTO.class))}),
        @ApiResponse(responseCode = "404", description = "Estado no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<HistorialEstadoTransaccionDTO> obtenerEstadoPorId(
            @Parameter(description = "ID del estado") @PathVariable Integer id) {
        try {
            return ResponseEntity.ok(service.obtenerEstadoPorId(id));
        } catch (RuntimeException e) {
            throw new NotFoundException(id.toString(), ENTITY_NAME);
        }
    }

    @Operation(summary = "Registrar cambio de estado", 
               description = "Registra un nuevo cambio de estado para una transacción")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cambio de estado registrado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Error en el registro del cambio de estado"),
        @ApiResponse(responseCode = "404", description = "Transacción no encontrada")
    })
    @PostMapping("/transaccion/{codigoTransaccion}/estado")
    public ResponseEntity<TransaccionResponseDTO> registrarCambioEstado(
            @Parameter(description = "Código de la transacción") 
            @PathVariable Integer codigoTransaccion,
            @Parameter(description = "Nuevo estado") 
            @RequestParam String estado,
            @Parameter(description = "Detalle del cambio") 
            @RequestParam(required = false) String detalle) {
        try {
            HistorialEstadoTransaccionDTO historial = service.registrarCambioEstado(
                codigoTransaccion, estado, detalle != null ? detalle : "Cambio de estado manual");
            return ResponseEntity.ok(new TransaccionResponseDTO("Estado registrado exitosamente", 
                historial.getEstado(), null));
        } catch (RuntimeException e) {
            throw new BusinessException(codigoTransaccion.toString(), ENTITY_NAME, "registrar cambio estado");
        }
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<TransaccionResponseDTO> handleNotFoundException(NotFoundException e) {
        return ResponseEntity.status(404)
            .body(new TransaccionResponseDTO(e.getMessage(), true));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<TransaccionResponseDTO> handleBusinessException(BusinessException e) {
        return ResponseEntity.status(400)
            .body(new TransaccionResponseDTO(e.getMessage(), true));
    }
} 