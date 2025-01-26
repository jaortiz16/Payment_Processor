package com.banquito.cards.transaccion.controller;

import com.banquito.cards.transaccion.service.HistorialEstadoTransaccionService;
import com.banquito.cards.transaccion.controller.dto.HistorialEstadoTransaccionDTO;
import com.banquito.cards.transaccion.controller.dto.TransaccionResponseDTO;
import com.banquito.cards.transaccion.controller.mapper.HistorialEstadoTransaccionMapper;
import com.banquito.cards.exception.NotFoundException;
import com.banquito.cards.exception.BusinessException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

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

@Slf4j
@Tag(name = "Historial de Estados", description = "API para la gestión del historial de estados de transacciones")
@RestController
@RequestMapping("/v1/historiales")
@Validated
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class HistorialEstadoTransaccionController {

    private static final String ENTITY_NAME = "HistorialEstadoTransaccion";
    private final HistorialEstadoTransaccionService service;
    private final HistorialEstadoTransaccionMapper mapper;

    public HistorialEstadoTransaccionController(HistorialEstadoTransaccionService service, 
                                              HistorialEstadoTransaccionMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @Operation(summary = "Listar historial de estados", 
               description = "Retorna todos los estados de transacciones con paginación, filtros y ordenamiento")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de estados obtenida exitosamente",
                    content = {@Content(schema = @Schema(implementation = HistorialEstadoTransaccionDTO.class))}),
        @ApiResponse(responseCode = "400", description = "Parámetros de búsqueda inválidos")
    })
    @GetMapping
    public ResponseEntity<Page<HistorialEstadoTransaccionDTO>> listarHistorial(
            @Parameter(description = "Número de página (0..N)") 
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de la página") 
            @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Campo por el cual ordenar") 
            @RequestParam(defaultValue = "fechaEstadoCambio") String sortBy,
            @Parameter(description = "Dirección del ordenamiento (asc o desc)") 
            @RequestParam(defaultValue = "desc") String sortDir,
            @Parameter(description = "Estado (PEN, APR, REC, REV, PRO)") 
            @RequestParam(required = false) 
            @Pattern(regexp = "PEN|APR|REC|REV|PRO") String estado,
            @Parameter(description = "Fecha inicial") 
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @Parameter(description = "Fecha final") 
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        try {
            log.info("Listando historial con filtros: estado={}, fechaInicio={}, fechaFin={}, page={}, size={}", 
                estado, fechaInicio, fechaFin, page, size);
            Sort.Direction direction = Sort.Direction.fromString(sortDir.toUpperCase());
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
            
            return ResponseEntity.ok(service.listarHistorial(estado, fechaInicio, fechaFin, pageable));
        } catch (RuntimeException e) {
            log.error("Error al listar historial", e);
            throw new BusinessException("listar historial", ENTITY_NAME, e.getMessage());
        }
    }

    @Operation(summary = "Obtener historial por transacción", 
               description = "Retorna el historial de estados de una transacción específica")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Historial obtenido exitosamente"),
        @ApiResponse(responseCode = "404", description = "Transacción no encontrada")
    })
    @GetMapping("/transacciones/{codigoTransaccion}")
    public ResponseEntity<Page<HistorialEstadoTransaccionDTO>> obtenerHistorialPorTransaccion(
            @Parameter(description = "Código de la transacción") 
            @PathVariable Integer codigoTransaccion,
            @Parameter(description = "Estado (PEN, APR, REC, REV, PRO)") 
            @RequestParam(required = false) 
            @Pattern(regexp = "PEN|APR|REC|REV|PRO") String estado,
            @Parameter(description = "Número de página (0..N)") 
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de la página") 
            @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Campo por el cual ordenar") 
            @RequestParam(defaultValue = "fechaEstadoCambio") String sortBy,
            @Parameter(description = "Dirección del ordenamiento (asc o desc)") 
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            log.info("Obteniendo historial para transacción: {}", codigoTransaccion);
            Sort.Direction direction = Sort.Direction.fromString(sortDir.toUpperCase());
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
            
            return ResponseEntity.ok(service.obtenerHistorialPorTransaccion(codigoTransaccion, estado, pageable));
        } catch (RuntimeException e) {
            log.error("Error al obtener historial para transacción: {}", codigoTransaccion, e);
            throw new BusinessException(codigoTransaccion.toString(), ENTITY_NAME, "obtener historial");
        }
    }

    @Operation(summary = "Obtener estado por ID", 
               description = "Retorna un estado específico del historial")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Estado encontrado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Estado no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<HistorialEstadoTransaccionDTO> obtenerEstadoPorId(
            @Parameter(description = "ID del estado") 
            @PathVariable Integer id) {
        try {
            log.info("Obteniendo estado: {}", id);
            return ResponseEntity.ok(service.obtenerEstadoPorId(id));
        } catch (RuntimeException e) {
            log.error("Error al obtener estado: {}", id, e);
            throw new NotFoundException(id.toString(), ENTITY_NAME);
        }
    }

    @Operation(summary = "Registrar cambio de estado", 
               description = "Registra un nuevo cambio de estado para una transacción")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cambio de estado registrado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Error en los datos del cambio de estado"),
        @ApiResponse(responseCode = "404", description = "Transacción no encontrada")
    })
    @PostMapping("/transacciones/{transaccionId}/estados")
    public ResponseEntity<TransaccionResponseDTO> registrarCambioEstado(
            @Parameter(description = "ID de la transacción") 
            @PathVariable Integer transaccionId,
            @Parameter(description = "Nuevo estado (PEN, APR, REC, REV, PRO)") 
            @RequestParam @Pattern(regexp = "PEN|APR|REC|REV|PRO") String nuevoEstado,
            @Parameter(description = "Detalle del cambio de estado") 
            @RequestParam(required = false) String detalle) {
        try {
            log.info("Registrando cambio de estado para transacción {}: {} - {}", 
                transaccionId, nuevoEstado, detalle);
            HistorialEstadoTransaccionDTO historial = service.registrarCambioEstado(
                transaccionId, nuevoEstado, detalle != null ? detalle : "");
            return ResponseEntity.ok(new TransaccionResponseDTO("Estado registrado exitosamente"));
        } catch (RuntimeException e) {
            log.error("Error al registrar estado para transacción: {}", transaccionId, e);
            throw new BusinessException(transaccionId.toString(), ENTITY_NAME, "registrar estado");
        }
    }

    @ExceptionHandler({NotFoundException.class})
    public ResponseEntity<TransaccionResponseDTO> handleNotFoundException(NotFoundException e) {
        log.warn("Recurso no encontrado: {}", e.getMessage());
        return ResponseEntity.status(404)
            .body(new TransaccionResponseDTO(e.getMessage(), true));
    }

    @ExceptionHandler({BusinessException.class})
    public ResponseEntity<TransaccionResponseDTO> handleBusinessException(BusinessException e) {
        log.error("Error de negocio: {}", e.getMessage());
        return ResponseEntity.status(400)
            .body(new TransaccionResponseDTO(e.getMessage(), true));
    }
} 