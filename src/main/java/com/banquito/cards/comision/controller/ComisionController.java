package com.banquito.cards.comision.controller;

import com.banquito.cards.comision.controller.dto.ComisionDTO;
import com.banquito.cards.comision.controller.dto.ComisionSegmentoDTO;
import com.banquito.cards.comision.controller.mapper.ComisionMapper;
import com.banquito.cards.comision.model.Comision;
import com.banquito.cards.comision.model.ComisionSegmento;
import com.banquito.cards.comision.service.ComisionService;
import com.banquito.cards.exception.NotFoundException;
import com.banquito.cards.exception.BusinessException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Comisiones", description = "API para la gestión de comisiones y sus segmentos")
@RestController
@RequestMapping("/v1/comisiones")
public class ComisionController {

    private static final String ENTITY_NAME = "Comision";
    private final ComisionService comisionService;
    private final ComisionMapper comisionMapper;

    public ComisionController(ComisionService comisionService, ComisionMapper comisionMapper) {
        this.comisionService = comisionService;
        this.comisionMapper = comisionMapper;
    }

    @Operation(summary = "Listar comisiones", description = "Retorna todas las comisiones con opciones de filtrado y ordenamiento")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de comisiones obtenida exitosamente",
                    content = {@Content(schema = @Schema(implementation = ComisionDTO.class))}),
        @ApiResponse(responseCode = "400", description = "Parámetros de filtrado inválidos")
    })
    @GetMapping
    public ResponseEntity<List<ComisionDTO>> listarComisiones(
            @Parameter(description = "Tipo de comisión (POR o FIJ)") 
            @RequestParam(required = false) String tipo,
            @Parameter(description = "Monto base mínimo") 
            @RequestParam(required = false) BigDecimal montoMinimo,
            @Parameter(description = "Monto base máximo") 
            @RequestParam(required = false) BigDecimal montoMaximo,
            @Parameter(description = "Campo por el cual ordenar (tipo, montoBase, transaccionesBase)") 
            @RequestParam(required = false, defaultValue = "codigo") String sortBy,
            @Parameter(description = "Dirección del ordenamiento (asc o desc)") 
            @RequestParam(required = false, defaultValue = "asc") String sortDir) {
        try {
            List<Comision> comisiones;
            if (tipo != null) {
                comisiones = comisionService.obtenerComisionesPorTipo(tipo);
            } else if (montoMinimo != null && montoMaximo != null) {
                comisiones = comisionService.obtenerComisionesPorMontoBaseEntre(montoMinimo, montoMaximo);
            } else {
                comisiones = comisionService.obtenerTodasLasComisiones(sortBy, sortDir);
            }
            
            List<ComisionDTO> comisionesDTO = comisiones.stream()
                    .map(comisionMapper::toDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(comisionesDTO);
        } catch (RuntimeException e) {
            throw new BusinessException("listar comisiones", ENTITY_NAME, e.getMessage());
        }
    }

    @Operation(summary = "Obtener comisión por ID", description = "Retorna una comisión específica basada en su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Comisión encontrada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Comisión no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ComisionDTO> obtenerComision(
            @Parameter(description = "ID de la comisión") 
            @PathVariable Integer id) {
        try {
            Comision comision = comisionService.obtenerComisionPorId(id);
            return ResponseEntity.ok(comisionMapper.toDTO(comision));
        } catch (RuntimeException e) {
            throw new NotFoundException(id.toString(), ENTITY_NAME);
        }
    }

    @Operation(summary = "Crear nueva comisión", description = "Registra una nueva comisión en el sistema")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Comisión creada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Error en los datos de la comisión")
    })
    @PostMapping
    public ResponseEntity<ComisionDTO> crearComision(
            @Parameter(description = "Datos de la comisión") 
            @Valid @RequestBody ComisionDTO comisionDTO) {
        try {
            Comision comision = comisionMapper.toModel(comisionDTO);
            Comision comisionCreada = comisionService.crearComision(comision);
            return ResponseEntity.ok(comisionMapper.toDTO(comisionCreada));
        } catch (RuntimeException e) {
            throw new BusinessException(comisionDTO.getTipo(), ENTITY_NAME, "crear");
        }
    }

    @Operation(summary = "Actualizar comisión", description = "Actualiza una comisión existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Comisión actualizada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Comisión no encontrada")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ComisionDTO> actualizarComision(
            @Parameter(description = "ID de la comisión") 
            @PathVariable Integer id,
            @Parameter(description = "Datos actualizados de la comisión") 
            @Valid @RequestBody ComisionDTO comisionDTO) {
        try {
            Comision comision = comisionMapper.toModel(comisionDTO);
            Comision comisionActualizada = comisionService.actualizarComision(id, comision);
            return ResponseEntity.ok(comisionMapper.toDTO(comisionActualizada));
        } catch (RuntimeException e) {
            throw new BusinessException(id.toString(), ENTITY_NAME, "actualizar");
        }
    }

    @Operation(summary = "Agregar segmento a comisión", description = "Agrega un nuevo segmento a una comisión existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Segmento agregado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Comisión no encontrada")
    })
    @PostMapping("/{id}/segmentos")
    public ResponseEntity<ComisionSegmentoDTO> agregarSegmento(
            @Parameter(description = "ID de la comisión") 
            @PathVariable Integer id,
            @Parameter(description = "Datos del segmento") 
            @Valid @RequestBody ComisionSegmentoDTO segmentoDTO) {
        try {
            ComisionSegmento segmento = comisionMapper.segmentoToModel(segmentoDTO);
            ComisionSegmento segmentoCreado = comisionService.agregarSegmento(id, segmento);
            return ResponseEntity.ok(comisionMapper.segmentoToDTO(segmentoCreado));
        } catch (RuntimeException e) {
            throw new BusinessException(id.toString(), ENTITY_NAME, "agregar segmento");
        }
    }

    @ExceptionHandler({NotFoundException.class})
    public ResponseEntity<String> handleNotFoundException(NotFoundException e) {
        return ResponseEntity.status(404).body(e.getMessage());
    }

    @ExceptionHandler({BusinessException.class})
    public ResponseEntity<String> handleBusinessException(BusinessException e) {
        return ResponseEntity.status(400).body(e.getMessage());
    }
}