package com.banquito.cards.transaccion.controller;

import com.banquito.cards.transaccion.model.Transaccion;
import com.banquito.cards.transaccion.model.HistorialEstadoTransaccion;
import com.banquito.cards.transaccion.repository.HistorialEstadoTransaccionRepository;
import com.banquito.cards.transaccion.service.TransaccionService;
import com.banquito.cards.transaccion.controller.dto.TransaccionDTO;
import com.banquito.cards.transaccion.controller.mapper.TransaccionMapper;

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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Tag(name = "Transacciones", description = "API para la gestión de transacciones con tarjetas")
@RestController
@RequestMapping("/api/v1/transacciones")
public class TransaccionController {

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
    public ResponseEntity<?> obtenerTransaccion(@PathVariable Integer id) {
        try {
            log.info("Buscando transacción con ID: {}", id);
            TransaccionDTO transaccion = transaccionService.obtenerTransaccionPorId(id);
            log.info("Transacción encontrada: {}", transaccion);
            return ResponseEntity.ok(transaccion);
        } catch (RuntimeException e) {
            log.error("Error al buscar transacción con ID: {}", id, e);
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.status(404).body(response);
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
    public ResponseEntity<?> actualizarEstado(
            @Parameter(description = "ID de la transacción") @PathVariable Integer id,
            @Parameter(description = "Nuevo estado de la transacción") @RequestParam String nuevoEstado,
            @Parameter(description = "Detalle del cambio de estado") @RequestParam(required = false) String detalle) {
        try {
            log.info("Actualizando estado de transacción con ID: {}, nuevoEstado: {}", id, nuevoEstado);
            TransaccionDTO updatedTransaccion = transaccionService.actualizarEstadoTransaccion(
                    id, nuevoEstado, detalle != null ? detalle : "Cambio de estado manual");
            log.info("Estado actualizado exitosamente: {}", updatedTransaccion);
            return ResponseEntity.ok(updatedTransaccion);
        } catch (RuntimeException e) {
            log.error("Error al actualizar estado de transacción con ID: {}", id, e);
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
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
    public ResponseEntity<?> buscarPorEstadoYFecha(@RequestParam String estado,
                                                   @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
                                                   @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        try {
            log.info("Buscando transacciones con estado: {}, entre fechas: {} y {}", estado, fechaInicio, fechaFin);
            List<TransaccionDTO> transacciones = transaccionService.obtenerTransaccionesPorEstadoYFecha(estado, fechaInicio, fechaFin);
            log.info("Transacciones encontradas: {}", transacciones.size());
            return ResponseEntity.ok(transacciones);
        } catch (RuntimeException e) {
            log.error("Error al buscar transacciones por estado y fecha", e);
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
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
    public ResponseEntity<?> buscarPorBancoYMonto(
            @Parameter(description = "Código del banco a buscar") 
            @RequestParam Integer codigoBanco,
            @Parameter(description = "Monto mínimo de la transacción") 
            @RequestParam(required = false) BigDecimal montoMinimo,
            @Parameter(description = "Monto máximo de la transacción") 
            @RequestParam(required = false) BigDecimal montoMaximo) {
        try {
            log.info("Buscando transacciones para banco: {}, rango de montos: {} - {}", codigoBanco, montoMinimo, montoMaximo);
            List<TransaccionDTO> transacciones = transaccionService.obtenerTransaccionesPorBancoYMonto(codigoBanco, montoMinimo, montoMaximo);
            log.info("Transacciones encontradas: {}", transacciones.size());
            return ResponseEntity.ok(transacciones);
        } catch (RuntimeException e) {
            log.error("Error al buscar transacciones por banco y monto", e);
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Operation(summary = "Crear nueva transacción", 
               description = "Crea una nueva transacción y la procesa con el banco")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Transacción creada y aprobada"),
        @ApiResponse(responseCode = "400", description = "Error en la creación o procesamiento de la transacción")
    })
    @PostMapping
    public ResponseEntity<?> crearTransaccion(@RequestBody TransaccionDTO transaccionDTO) {
        try {
            log.info("Creando nueva transacción: {}", transaccionDTO);
            transaccionDTO.setEstado("PEN");
            transaccionDTO.setFechaCreacion(LocalDateTime.now());
            TransaccionDTO transaccionGuardada = transaccionService.guardarTransaccion(transaccionDTO);

            log.info("Procesando transacción con banco: {}", transaccionGuardada.getCodigo());
            transaccionService.procesarConBanco(transaccionGuardada.getCodigo());

            TransaccionDTO transaccionFinal = transaccionService.obtenerTransaccionPorId(transaccionGuardada.getCodigo());
            List<HistorialEstadoTransaccion> historiales = historialRepository.findByTransaccionCodigoOrderByFechaEstadoCambioDesc(
                    transaccionFinal.getCodigo());

            String detalle = !historiales.isEmpty() ? historiales.get(0).getDetalle() : null;
            Map<String, String> response = new HashMap<>();

            if ("APR".equals(transaccionFinal.getEstado())) {
                response.put("mensaje", "Transacción aceptada");
                log.info("Transacción aceptada: {}", transaccionFinal);
                return ResponseEntity.status(201).body(response);
            } else if ("REC".equals(transaccionFinal.getEstado())) {
                response.put("mensaje", "Transacción rechazada");
                if (detalle != null) {
                    response.put("detalle", detalle);
                }
                log.warn("Transacción rechazada: {}, detalle: {}", transaccionFinal, detalle);
                return ResponseEntity.status(400).body(response);
            } else {
                response.put("mensaje", "Estado de transacción desconocido");
                log.error("Estado desconocido para transacción: {}", transaccionFinal);
                return ResponseEntity.status(400).body(response);
            }
        } catch (Exception e) {
            log.error("Error al crear transacción", e);
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.status(400).body(response);
        }
    }

    @Operation(summary = "Procesar respuesta de fraude", 
               description = "Procesa la respuesta del sistema de fraude para una transacción")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Respuesta de fraude procesada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Error en el procesamiento de la respuesta de fraude")
    })
    @PostMapping("/{codigoUnicoTransaccion}/fraude")
    public ResponseEntity<Map<String, String>> procesarRespuestaFraude(
            @Parameter(description = "Código único de la transacción") @PathVariable String codigoUnicoTransaccion,
            @Parameter(description = "Decisión del sistema de fraude (APROBAR/RECHAZAR)") @RequestParam String decision) {
        try {
            log.info("Procesando respuesta de fraude para transacción: {}, decisión: {}", codigoUnicoTransaccion, decision);
            TransaccionDTO result = transaccionService.procesarRespuestaFraude(codigoUnicoTransaccion, decision);

            String mensaje;
            switch (result.getEstado()) {
                case "APR":
                    mensaje = "Transacción aceptada";
                    log.info("Transacción aceptada por sistema de fraude: {}", codigoUnicoTransaccion);
                    break;
                case "REC":
                    mensaje = "Transacción rechazada";
                    log.warn("Transacción rechazada por sistema de fraude: {}", codigoUnicoTransaccion);
                    break;
                default:
                    mensaje = "Estado de transacción desconocido";
                    log.error("Estado desconocido para transacción: {}", codigoUnicoTransaccion);
            }

            Map<String, String> response = new HashMap<>();
            response.put("mensaje", mensaje);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error al procesar respuesta de fraude para transacción: {}", codigoUnicoTransaccion, e);
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.status(400).body(response);
        }
    }
}