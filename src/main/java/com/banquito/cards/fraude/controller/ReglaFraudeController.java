package com.banquito.cards.fraude.controller;

import com.banquito.cards.fraude.model.ReglaFraude;
import com.banquito.cards.fraude.service.ReglaFraudeService;
import com.banquito.cards.fraude.controller.dto.ReglaFraudeDTO;
import com.banquito.cards.fraude.controller.dto.FraudeResponseDTO;
import com.banquito.cards.fraude.controller.mapper.ReglaFraudeMapper;
import com.banquito.cards.exception.NotFoundException;
import com.banquito.cards.exception.BusinessException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Reglas de Fraude", description = "API para la gestión de reglas de detección de fraude")
@RestController
@RequestMapping("/v1/fraudes/reglas")
public class ReglaFraudeController {

    private static final String ENTITY_NAME = "ReglaFraude";
    private final ReglaFraudeService reglaFraudeService;
    private final ReglaFraudeMapper reglaFraudeMapper;

    public ReglaFraudeController(ReglaFraudeService reglaFraudeService, ReglaFraudeMapper reglaFraudeMapper) {
        this.reglaFraudeService = reglaFraudeService;
        this.reglaFraudeMapper = reglaFraudeMapper;
    }

    @Operation(summary = "Listar reglas de fraude", description = "Retorna todas las reglas de fraude con opciones de filtrado y ordenamiento")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de reglas obtenida exitosamente",
                    content = {@Content(schema = @Schema(implementation = ReglaFraudeDTO.class))}),
        @ApiResponse(responseCode = "400", description = "Parámetros de filtrado inválidos")
    })
    @GetMapping
    public ResponseEntity<List<ReglaFraudeDTO>> listarReglas(
            @Parameter(description = "Estado de la regla (ACT o INA)") 
            @RequestParam(required = false, defaultValue = "ACT") String estado,
            @Parameter(description = "Tipo de regla (TRX, MNT, GEO, COM, HOR)") 
            @RequestParam(required = false) String tipoRegla,
            @Parameter(description = "Campo por el cual ordenar") 
            @RequestParam(required = false, defaultValue = "prioridad") String sortBy,
            @Parameter(description = "Dirección del ordenamiento (asc o desc)") 
            @RequestParam(required = false, defaultValue = "asc") String sortDir) {
        try {
            List<ReglaFraude> reglas;
            if (tipoRegla != null) {
                reglas = reglaFraudeService.obtenerReglasPorTipo(tipoRegla);
            } else {
                reglas = estado.equals("ACT") ? 
                    reglaFraudeService.obtenerReglasActivas() : 
                    reglaFraudeService.obtenerTodasLasReglas();
            }
            
            List<ReglaFraudeDTO> reglasDTO = reglas.stream()
                    .map(reglaFraudeMapper::toDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(reglasDTO);
        } catch (RuntimeException e) {
            throw new BusinessException("listar reglas", ENTITY_NAME, e.getMessage());
        }
    }

    @Operation(summary = "Obtener regla por ID", description = "Retorna una regla específica basada en su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Regla encontrada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Regla no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ReglaFraudeDTO> obtenerRegla(
            @Parameter(description = "ID de la regla") 
            @PathVariable Integer id) {
        try {
            ReglaFraude regla = reglaFraudeService.obtenerReglaPorId(id)
                    .orElseThrow(() -> new NotFoundException(id.toString(), ENTITY_NAME));
            return ResponseEntity.ok(reglaFraudeMapper.toDTO(regla));
        } catch (RuntimeException e) {
            throw new BusinessException(id.toString(), ENTITY_NAME, "obtener");
        }
    }

    @Operation(summary = "Crear regla de fraude", description = "Registra una nueva regla de fraude en el sistema")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Regla creada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Error en los datos de la regla")
    })
    @PostMapping
    public ResponseEntity<ReglaFraudeDTO> crearRegla(
            @Parameter(description = "Datos de la regla") 
            @Valid @RequestBody ReglaFraudeDTO reglaDTO) {
        try {
            ReglaFraude regla = reglaFraudeMapper.toModel(reglaDTO);
            ReglaFraude reglaCreada = reglaFraudeService.crearRegla(regla);
            return ResponseEntity.ok(reglaFraudeMapper.toDTO(reglaCreada));
        } catch (RuntimeException e) {
            throw new BusinessException(reglaDTO.getNombreRegla(), ENTITY_NAME, "crear");
        }
    }

    @Operation(summary = "Actualizar regla de fraude", description = "Actualiza una regla de fraude existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Regla actualizada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Regla no encontrada")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ReglaFraudeDTO> actualizarRegla(
            @Parameter(description = "ID de la regla") 
            @PathVariable Integer id,
            @Parameter(description = "Datos actualizados de la regla") 
            @Valid @RequestBody ReglaFraudeDTO reglaDTO) {
        try {
            ReglaFraude regla = reglaFraudeMapper.toModel(reglaDTO);
            ReglaFraude reglaActualizada = reglaFraudeService.actualizarRegla(id, regla);
            return ResponseEntity.ok(reglaFraudeMapper.toDTO(reglaActualizada));
        } catch (RuntimeException e) {
            throw new BusinessException(id.toString(), ENTITY_NAME, "actualizar");
        }
    }

    @Operation(summary = "Desactivar regla de fraude", description = "Cambia el estado de una regla de fraude a inactiva")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Regla desactivada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Regla no encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<FraudeResponseDTO> desactivarRegla(
            @Parameter(description = "ID de la regla") 
            @PathVariable Integer id) {
        try {
            reglaFraudeService.desactivarRegla(id);
            return ResponseEntity.ok(new FraudeResponseDTO("Regla desactivada exitosamente"));
        } catch (RuntimeException e) {
            throw new BusinessException(id.toString(), ENTITY_NAME, "desactivar");
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