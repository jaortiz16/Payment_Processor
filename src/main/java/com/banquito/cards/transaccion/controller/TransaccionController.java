package com.banquito.cards.transaccion.controller;

import com.banquito.cards.transaccion.model.HistorialEstadoTransaccion;
import com.banquito.cards.transaccion.repository.HistorialEstadoTransaccionRepository;
import com.banquito.cards.transaccion.service.TransaccionService;
import com.banquito.cards.transaccion.controller.dto.TransaccionDTO;
import com.banquito.cards.transaccion.controller.dto.TransaccionResponseDTO;
import com.banquito.cards.transaccion.controller.mapper.TransaccionMapper;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "Transacciones", description = "API para la gestión de transacciones con tarjetas")
@RestController
@RequestMapping("/v1/transacciones")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class TransaccionController {

    private static final Logger log = LoggerFactory.getLogger(TransaccionController.class);
    private static final String ENTITY_NAME = "Transaccion";

    private final TransaccionService transaccionService;
    private final HistorialEstadoTransaccionRepository historialRepository;
    private final TransaccionMapper transaccionMapper;

    public TransaccionController(TransaccionService transaccionService,
                               HistorialEstadoTransaccionRepository historialRepository,
                               TransaccionMapper transaccionMapper) {
        this.transaccionService = transaccionService;
        this.historialRepository = historialRepository;
        this.transaccionMapper = transaccionMapper;
    }

    @Operation(summary = "Obtener transacción por ID", 
               description = "Retorna una transacción específica basada en su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Transacción encontrada",
                    content = {@Content(schema = @Schema(implementation = TransaccionDTO.class))}),
        @ApiResponse(responseCode = "404", description = "Transacción no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<TransaccionDTO> obtenerTransaccion(@PathVariable Integer id) {
        try {
            TransaccionDTO transaccion = transaccionService.obtenerTransaccionPorId(id);
            return ResponseEntity.ok(transaccion);
        } catch (RuntimeException e) {
            throw new NotFoundException(id.toString(), ENTITY_NAME);
        }
    }

    @Operation(summary = "Actualizar estado de transacción", 
               description = "Actualiza el estado de una transacción existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Estado actualizado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Error en la actualización del estado"),
        @ApiResponse(responseCode = "404", description = "Transacción no encontrada")
    })
    @PutMapping("/{id}/estado")
    public ResponseEntity<TransaccionResponseDTO> actualizarEstado(
            @Parameter(description = "ID de la transacción") @PathVariable Integer id,
            @Parameter(description = "Nuevo estado de la transacción") @RequestParam String nuevoEstado,
            @Parameter(description = "Detalle del cambio de estado") @RequestParam(required = false) String detalle) {
        try {
            TransaccionDTO transaccion = transaccionService.actualizarEstadoTransaccion(id, nuevoEstado, 
                detalle != null ? detalle : "Cambio de estado manual");
            return ResponseEntity.ok(new TransaccionResponseDTO("Estado actualizado exitosamente", 
                transaccion.getEstado(), transaccion.getMonto()));
        } catch (RuntimeException e) {
            throw new BusinessException(id.toString(), ENTITY_NAME, "actualizar estado");
        }
    }

    @Operation(summary = "Buscar transacciones por estado y fecha", 
               description = "Retorna todas las transacciones que coincidan con el estado y rango de fechas especificado")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Búsqueda realizada exitosamente",
                    content = {@Content(schema = @Schema(implementation = TransaccionDTO.class))}),
        @ApiResponse(responseCode = "400", description = "Error en los parámetros de búsqueda")
    })
    @GetMapping("/buscar-por-estado-fecha")
    public ResponseEntity<List<TransaccionDTO>> buscarPorEstadoYFecha(
            @Parameter(description = "Estado de la transacción a buscar") 
            @RequestParam String estado,
            @Parameter(description = "Fecha inicial del rango de búsqueda") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @Parameter(description = "Fecha final del rango de búsqueda") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        try {
            return ResponseEntity.ok(
                transaccionService.obtenerTransaccionesPorEstadoYFecha(estado, fechaInicio, fechaFin));
        } catch (RuntimeException e) {
            throw new BusinessException(estado, ENTITY_NAME, "buscar por estado y fecha");
        }
    }

    @Operation(summary = "Buscar transacciones por banco y monto", 
               description = "Retorna todas las transacciones que coincidan con el banco y rango de montos especificados")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Búsqueda realizada exitosamente",
                    content = {@Content(schema = @Schema(implementation = TransaccionDTO.class))}),
        @ApiResponse(responseCode = "400", description = "Error en los parámetros de búsqueda")
    })
    @GetMapping("/buscar-por-banco-monto")
    public ResponseEntity<List<TransaccionDTO>> buscarPorBancoYMonto(
            @Parameter(description = "Código del banco a buscar") 
            @RequestParam Integer codigoBanco,
            @Parameter(description = "Monto mínimo de la transacción") 
            @RequestParam(required = false) BigDecimal montoMinimo,
            @Parameter(description = "Monto máximo de la transacción") 
            @RequestParam(required = false) BigDecimal montoMaximo) {
        try {
            return ResponseEntity.ok(
                transaccionService.obtenerTransaccionesPorBancoYMonto(codigoBanco, montoMinimo, montoMaximo));
        } catch (RuntimeException e) {
            throw new BusinessException(codigoBanco.toString(), ENTITY_NAME, "buscar por banco y monto");
        }
    }

    @Operation(summary = "Crear nueva transacción", 
               description = "Crea una nueva transacción y la procesa con el banco")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Transacción creada y aprobada"),
        @ApiResponse(responseCode = "400", description = "Error en la creación o procesamiento de la transacción")
    })
    @PostMapping
    public ResponseEntity<TransaccionResponseDTO> crearTransaccion(@RequestBody TransaccionDTO transaccionDTO) {
        try {
            transaccionDTO.setEstado("PEN");
            transaccionDTO.setFechaCreacion(LocalDateTime.now());
            TransaccionDTO transaccionGuardada = transaccionService.guardarTransaccion(transaccionDTO);

            try {
                transaccionService.procesarConBanco(transaccionGuardada.getCodigo());
                
                TransaccionDTO transaccionFinal = transaccionService.obtenerTransaccionPorId(transaccionGuardada.getCodigo());
                List<HistorialEstadoTransaccion> historiales = historialRepository.findByTransaccionCodigoOrderByFechaEstadoCambioDesc(
                    transaccionFinal.getCodigo());
                String detalle = !historiales.isEmpty() ? historiales.get(0).getDetalle() : null;
                
                switch (transaccionFinal.getEstado()) {
                    case "APR":
                        return ResponseEntity.status(201)
                            .body(new TransaccionResponseDTO("Transacción aceptada", transaccionFinal.getEstado(), transaccionFinal.getMonto()));
                    case "REC":
                        return ResponseEntity.status(400)
                            .body(new TransaccionResponseDTO("Transacción rechazada: " + (detalle != null ? detalle : ""), true));
                    default:
                        return ResponseEntity.status(400)
                            .body(new TransaccionResponseDTO("Estado de transacción desconocido", true));
                }
            } catch (Exception e) {
                throw new BusinessException(transaccionGuardada.getCodigo().toString(), ENTITY_NAME, "procesar con banco");
            }
        } catch (Exception e) {
            throw new BusinessException("datos de transacción", ENTITY_NAME, "crear transacción");
        }
    }

    @Operation(summary = "Procesar respuesta de fraude", 
               description = "Procesa la respuesta del sistema de fraude para una transacción")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Respuesta de fraude procesada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Error en el procesamiento de la respuesta de fraude")
    })
    @PostMapping("/{codigoUnicoTransaccion}/fraude")
    public ResponseEntity<TransaccionResponseDTO> procesarRespuestaFraude(
            @Parameter(description = "Código único de la transacción") @PathVariable String codigoUnicoTransaccion,
            @Parameter(description = "Decisión del sistema de fraude (APROBAR/RECHAZAR)") @RequestParam String decision) {
        try {
            TransaccionDTO result = transaccionService.procesarRespuestaFraude(codigoUnicoTransaccion, decision);
            String mensaje;
            
            switch (result.getEstado()) {
                case "APR":
                    mensaje = "Transacción aceptada";
                    return ResponseEntity.ok(new TransaccionResponseDTO(mensaje, result.getEstado(), result.getMonto()));
                case "REC":
                    mensaje = "Transacción rechazada";
                    return ResponseEntity.ok(new TransaccionResponseDTO(mensaje, true));
                default:
                    return ResponseEntity.ok(new TransaccionResponseDTO("Estado de transacción desconocido", true));
            }
        } catch (RuntimeException e) {
            throw new BusinessException(codigoUnicoTransaccion, ENTITY_NAME, "procesar respuesta fraude");
        }
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<TransaccionResponseDTO> handleNotFoundException(NotFoundException e) {
        return ResponseEntity.status(404).body(new TransaccionResponseDTO(e.getMessage(), true));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<TransaccionResponseDTO> handleBusinessException(BusinessException e) {
        return ResponseEntity.status(400).body(new TransaccionResponseDTO(e.getMessage(), true));
    }
}