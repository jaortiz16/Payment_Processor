package com.banquito.cards.fraude.controller;

import com.banquito.cards.fraude.model.ReglaFraude;
import com.banquito.cards.fraude.service.ReglaFraudeService;
import com.banquito.cards.fraude.controller.dto.ReglaFraudeDTO;
import com.banquito.cards.fraude.controller.mapper.ReglaFraudeMapper;
import com.banquito.cards.exception.NotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/v1/reglas-fraude")
public class ReglaFraudeController {

    private static final String ENTITY_NAME = "ReglaFraude";
    private final ReglaFraudeService reglaFraudeService;
    private final ReglaFraudeMapper reglaFraudeMapper;

    public ReglaFraudeController(ReglaFraudeService reglaFraudeService, ReglaFraudeMapper reglaFraudeMapper) {
        this.reglaFraudeService = reglaFraudeService;
        this.reglaFraudeMapper = reglaFraudeMapper;
    }

    @GetMapping
    public ResponseEntity<List<ReglaFraudeDTO>> obtenerReglasFraudeActivas() {
        try {
            log.info("Obteniendo lista de reglas de fraude activas");
            List<ReglaFraudeDTO> reglas = reglaFraudeService.obtenerReglasActivas()
                .stream()
                .map(reglaFraudeMapper::toDTO)
                .collect(Collectors.toList());
            log.info("Se encontraron {} reglas activas", reglas.size());
            return ResponseEntity.ok(reglas);
        } catch (RuntimeException e) {
            log.error("Error al obtener reglas activas: {}", e.getMessage());
            throw new RuntimeException("Error al obtener reglas activas: " + e.getMessage());
        }
    }

    @GetMapping("/todas")
    public ResponseEntity<List<ReglaFraudeDTO>> obtenerTodasLasReglasFraude() {
        try {
            log.info("Obteniendo todas las reglas de fraude");
            List<ReglaFraudeDTO> reglas = reglaFraudeService.obtenerTodasLasReglas()
                .stream()
                .map(reglaFraudeMapper::toDTO)
                .collect(Collectors.toList());
            log.info("Se encontraron {} reglas en total", reglas.size());
            return ResponseEntity.ok(reglas);
        } catch (RuntimeException e) {
            log.error("Error al obtener todas las reglas: {}", e.getMessage());
            throw new RuntimeException("Error al obtener todas las reglas: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReglaFraudeDTO> obtenerReglaFraudePorId(@PathVariable Integer id) {
        try {
            log.info("Buscando regla de fraude por ID: {}", id);
            ReglaFraude regla = reglaFraudeService.obtenerReglaPorId(id)
                    .orElseThrow(() -> {
                        log.error("No se encontró regla con ID: {}", id);
                        return new NotFoundException(id.toString(), ENTITY_NAME);
                    });
            log.info("Regla encontrada con ID: {}", id);
            return ResponseEntity.ok(reglaFraudeMapper.toDTO(regla));
        } catch (NotFoundException e) {
            log.error("Regla no encontrada con ID {}: {}", id, e.getMessage());
            throw e;
        } catch (RuntimeException e) {
            log.error("Error al obtener la regla con ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Error al obtener la regla: " + e.getMessage());
        }
    }

    @GetMapping("/tipo/{tipoRegla}")
    public ResponseEntity<List<ReglaFraudeDTO>> obtenerReglasFraudePorTipo(@PathVariable String tipoRegla) {
        try {
            log.info("Buscando reglas de fraude por tipo: {}", tipoRegla);
            List<ReglaFraudeDTO> reglas = reglaFraudeService.obtenerReglasPorTipo(tipoRegla)
                .stream()
                .map(reglaFraudeMapper::toDTO)
                .collect(Collectors.toList());
            log.info("Se encontraron {} reglas del tipo: {}", reglas.size(), tipoRegla);
            return ResponseEntity.ok(reglas);
        } catch (RuntimeException e) {
            log.error("Error al obtener reglas por tipo {}: {}", tipoRegla, e.getMessage());
            throw new RuntimeException("Error al obtener reglas por tipo: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<ReglaFraudeDTO> crearReglaFraude(@Valid @RequestBody ReglaFraudeDTO reglaDTO) {
        try {
            log.info("Creando nueva regla de fraude");
            ReglaFraude regla = reglaFraudeMapper.toModel(reglaDTO);
            ReglaFraude reglaCreada = reglaFraudeService.crearRegla(regla);
            log.info("Regla creada exitosamente con ID: {}", reglaCreada.getId());
            return ResponseEntity.ok(reglaFraudeMapper.toDTO(reglaCreada));
        } catch (IllegalArgumentException e) {
            log.error("Error de validación al crear regla: {}", e.getMessage());
            throw new RuntimeException("Error de validación: " + e.getMessage());
        } catch (RuntimeException e) {
            log.error("Error al crear regla: {}", e.getMessage());
            throw new RuntimeException("Error al crear la regla: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReglaFraudeDTO> actualizarReglaFraude(
            @PathVariable Integer id,
            @Valid @RequestBody ReglaFraudeDTO reglaDTO) {
        try {
            log.info("Actualizando regla de fraude con ID: {}", id);
            ReglaFraude regla = reglaFraudeMapper.toModel(reglaDTO);
            ReglaFraude reglaActualizada = reglaFraudeService.actualizarRegla(id, regla);
            log.info("Regla actualizada exitosamente con ID: {}", id);
            return ResponseEntity.ok(reglaFraudeMapper.toDTO(reglaActualizada));
        } catch (NotFoundException e) {
            log.error("Regla no encontrada para actualizar con ID {}: {}", id, e.getMessage());
            throw e;
        } catch (IllegalArgumentException e) {
            log.error("Error de validación al actualizar regla con ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Error de validación: " + e.getMessage());
        } catch (RuntimeException e) {
            log.error("Error al actualizar regla con ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Error al actualizar la regla: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> desactivarReglaFraude(@PathVariable Integer id) {
        try {
            log.info("Desactivando regla de fraude con ID: {}", id);
            reglaFraudeService.desactivarRegla(id);
            log.info("Regla desactivada exitosamente con ID: {}", id);
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Regla desactivada exitosamente");
            return ResponseEntity.ok(response);
        } catch (NotFoundException e) {
            log.error("Regla no encontrada para desactivar con ID {}: {}", id, e.getMessage());
            throw e;
        } catch (RuntimeException e) {
            log.error("Error al desactivar regla con ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Error al desactivar la regla: " + e.getMessage());
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