package com.banquito.cards.comision.controller;

import com.banquito.cards.comision.controller.dto.ComisionDTO;
import com.banquito.cards.comision.controller.mapper.ComisionMapper;
import com.banquito.cards.comision.model.Comision;
import com.banquito.cards.comision.model.ComisionSegmento;
import com.banquito.cards.comision.service.ComisionService;
import com.banquito.cards.exception.NotFoundException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Tag(name = "Comisiones", description = "API para la gestión de comisiones y sus segmentos")
@RestController
@RequestMapping("/api/v1/comisiones")
public class ComisionController {

    private static final String ENTITY_NAME = "Comision";
    private final ComisionService comisionService;
    private final ComisionMapper comisionMapper;

    public ComisionController(ComisionService comisionService, ComisionMapper comisionMapper) {
        this.comisionService = comisionService;
        this.comisionMapper = comisionMapper;
    }

    @Operation(summary = "Obtener comisiones por tipo", description = "Retorna todas las comisiones que corresponden a un tipo específico")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de comisiones obtenida exitosamente", content = {
                    @Content(schema = @Schema(implementation = ComisionDTO.class)) }),
            @ApiResponse(responseCode = "404", description = "No se encontraron comisiones para el tipo especificado")
    })
    @GetMapping("/tipo-comision/{tipo}")
    public ResponseEntity<List<ComisionDTO>> obtenerPorTipo(
            @Parameter(description = "Tipo de comisión a buscar") @PathVariable String tipo) {
        try {
            log.info("Buscando comisiones por tipo: {}", tipo);
            List<Comision> comisiones = comisionService.obtenerComisionesPorTipo(tipo);
            List<ComisionDTO> comisionesDTO = comisiones.stream()
                    .map(comisionMapper::toDTO)
                    .collect(Collectors.toList());
            log.info("Se encontraron {} comisiones del tipo: {}", comisiones.size(), tipo);
            return ResponseEntity.ok(comisionesDTO);
        } catch (RuntimeException e) {
            log.error("Error al buscar comisiones por tipo {}: {}", tipo, e.getMessage());
            throw new NotFoundException(tipo, ENTITY_NAME);
        }
    }

    @Operation(summary = "Buscar comisiones por rango de monto", description = "Retorna las comisiones cuyo monto base está dentro del rango especificado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Búsqueda realizada exitosamente"),
            @ApiResponse(responseCode = "404", description = "No se encontraron comisiones en el rango especificado")
    })
    @GetMapping("/monto-comision")
    public ResponseEntity<List<ComisionDTO>> buscarPorMontoBase(
            @Parameter(description = "Monto mínimo de la comisión") @RequestParam BigDecimal montoMinimo,
            @Parameter(description = "Monto máximo de la comisión") @RequestParam BigDecimal montoMaximo) {
        try {
            log.info("Buscando comisiones por monto base entre {} y {}", montoMinimo, montoMaximo);
            List<Comision> comisiones = comisionService.obtenerComisionesPorMontoBaseEntre(montoMinimo, montoMaximo);
            List<ComisionDTO> comisionesDTO = comisiones.stream()
                    .map(comisionMapper::toDTO)
                    .collect(Collectors.toList());
            log.info("Se encontraron {} comisiones en el rango de montos especificado", comisiones.size());
            return ResponseEntity.ok(comisionesDTO);
        } catch (RuntimeException e) {
            log.error("Error al buscar comisiones por monto entre {} y {}: {}", montoMinimo, montoMaximo,
                    e.getMessage());
            throw new NotFoundException("monto entre " + montoMinimo + " y " + montoMaximo, ENTITY_NAME);
        }
    }

    @Operation(summary = "Obtener comisión por ID", description = "Retorna una comisión específica basada en su ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Comisión encontrada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Comisión no encontrada")
    })
    @GetMapping("/obtener-por-id/{id}")
    public ResponseEntity<ComisionDTO> obtenerPorId(
            @Parameter(description = "ID de la comisión") @PathVariable Integer id) {
        try {
            log.info("Buscando comisión por ID: {}", id);
            Comision comision = comisionService.obtenerComisionPorId(id);
            log.info("Comisión encontrada con ID: {}", id);
            return ResponseEntity.ok(comisionMapper.toDTO(comision));
        } catch (RuntimeException e) {
            log.error("Error al buscar comisión con ID {}: {}", id, e.getMessage());
            throw new NotFoundException(id.toString(), ENTITY_NAME);
        }
    }

    @Operation(summary = "Crear nueva comisión", description = "Registra una nueva comisión en el sistema")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Comisión creada exitosamente", content = {
                    @Content(schema = @Schema(implementation = ComisionDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Error en los datos de la comisión")
    })
    @PostMapping("/crear-comision")
    public ResponseEntity<ComisionDTO> crearComision(
            @Parameter(description = "Datos de la comisión a crear") @RequestBody ComisionDTO comisionDTO) {
        try {
            log.info("Creando nueva comisión de tipo: {}", comisionDTO.getTipo());
            Comision comision = comisionMapper.toModel(comisionDTO);
            Comision comisionCreada = comisionService.crearComision(comision);
            log.info("Comisión creada exitosamente con ID: {}", comisionCreada.getId());
            return ResponseEntity.ok(comisionMapper.toDTO(comisionCreada));
        } catch (RuntimeException e) {
            log.error("Error al crear comisión: {}", e.getMessage());
            throw new RuntimeException("Error al crear la comisión: " + e.getMessage());
        }
    }

    @Operation(summary = "Actualizar comisión", description = "Actualiza la información de una comisión existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Comisión actualizada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Error en los datos de la comisión"),
            @ApiResponse(responseCode = "404", description = "Comisión no encontrada")
    })
    @PutMapping("/actualizar-comision/{id}")
    public ResponseEntity<ComisionDTO> actualizarComision(
            @Parameter(description = "ID de la comisión a actualizar") @PathVariable Integer id,
            @Parameter(description = "Nuevos datos de la comisión") @RequestBody ComisionDTO comisionDTO) {
        try {
            log.info("Actualizando comisión con ID: {}", id);
            Comision comision = comisionMapper.toModel(comisionDTO);
            Comision comisionActualizada = comisionService.actualizarComision(id, comision);
            log.info("Comisión actualizada exitosamente con ID: {}", id);
            return ResponseEntity.ok(comisionMapper.toDTO(comisionActualizada));
        } catch (RuntimeException e) {
            log.error("Error al actualizar comisión con ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Error al actualizar la comisión: " + e.getMessage());
        }
    }

    @Operation(summary = "Agregar segmento a comisión", description = "Agrega un nuevo segmento a una comisión existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Segmento agregado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Error en los datos del segmento"),
            @ApiResponse(responseCode = "404", description = "Comisión no encontrada")
    })
    @PostMapping("/{id}/segmentos")
    public ResponseEntity<Map<String, String>> agregarSegmento(
            @Parameter(description = "ID de la comisión") @PathVariable Integer id,
            @Parameter(description = "Datos del segmento a agregar") @RequestBody ComisionSegmento segmento) {
        try {
            log.info("Agregando nuevo segmento a comisión con ID: {}", id);
            comisionService.agregarSegmento(id, segmento);
            log.info("Segmento agregado exitosamente a comisión con ID: {}", id);
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Segmento agregado exitosamente");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Error al agregar segmento a comisión con ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Error al agregar el segmento: " + e.getMessage());
        }
    }

    @Operation(summary = "Calcular comisión", description = "Calcula el monto de la comisión basado en el número de transacciones y monto")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cálculo realizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Error en los parámetros de cálculo"),
            @ApiResponse(responseCode = "404", description = "Comisión no encontrada")
    })
    @GetMapping("/{id}/calcular")
    public ResponseEntity<Map<String, BigDecimal>> calcularComision(
            @Parameter(description = "ID de la comisión") @PathVariable Integer id,
            @Parameter(description = "Número de transacciones") @RequestParam Integer numeroTransacciones,
            @Parameter(description = "Monto de la transacción") @RequestParam BigDecimal montoTransaccion) {
        try {
            log.info("Calculando comisión para ID: {}, número de transacciones: {}, monto: {}",
                    id, numeroTransacciones, montoTransaccion);
            BigDecimal comisionCalculada = comisionService.calcularComision(id, numeroTransacciones, montoTransaccion);
            log.info("Comisión calculada: {} para ID: {}", comisionCalculada, id);
            Map<String, BigDecimal> response = new HashMap<>();
            response.put("comision", comisionCalculada);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Error al calcular comisión para ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Error al calcular la comisión: " + e.getMessage());
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