package com.banquito.cards.fraude.controller;

import com.banquito.cards.fraude.model.ReglaFraude;
import com.banquito.cards.fraude.service.ReglaFraudeService;
import com.banquito.cards.fraude.controller.dto.ReglaFraudeDTO;
import com.banquito.cards.fraude.controller.mapper.ReglaFraudeMapper;
import com.banquito.cards.exception.NotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

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
            List<ReglaFraudeDTO> reglas = reglaFraudeService.obtenerReglasActivas()
                .stream()
                .map(reglaFraudeMapper::toDTO)
                .collect(Collectors.toList());
            return ResponseEntity.ok(reglas);
        } catch (RuntimeException e) {
            throw new RuntimeException("Error al obtener reglas activas: " + e.getMessage());
        }
    }

    @GetMapping("/todas")
    public ResponseEntity<List<ReglaFraudeDTO>> obtenerTodasLasReglasFraude() {
        try {
            List<ReglaFraudeDTO> reglas = reglaFraudeService.obtenerTodasLasReglas()
                .stream()
                .map(reglaFraudeMapper::toDTO)
                .collect(Collectors.toList());
            return ResponseEntity.ok(reglas);
        } catch (RuntimeException e) {
            throw new RuntimeException("Error al obtener todas las reglas: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReglaFraudeDTO> obtenerReglaFraudePorId(@PathVariable Integer id) {
        try {
            ReglaFraude regla = reglaFraudeService.obtenerReglaPorId(id)
                    .orElseThrow(() -> new NotFoundException(id.toString(), ENTITY_NAME));
            return ResponseEntity.ok(reglaFraudeMapper.toDTO(regla));
        } catch (NotFoundException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new RuntimeException("Error al obtener la regla: " + e.getMessage());
        }
    }

    @GetMapping("/tipo/{tipoRegla}")
    public ResponseEntity<List<ReglaFraudeDTO>> obtenerReglasFraudePorTipo(@PathVariable String tipoRegla) {
        try {
            List<ReglaFraudeDTO> reglas = reglaFraudeService.obtenerReglasPorTipo(tipoRegla)
                .stream()
                .map(reglaFraudeMapper::toDTO)
                .collect(Collectors.toList());
            return ResponseEntity.ok(reglas);
        } catch (RuntimeException e) {
            throw new RuntimeException("Error al obtener reglas por tipo: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<ReglaFraudeDTO> crearReglaFraude(@Valid @RequestBody ReglaFraudeDTO reglaDTO) {
        try {
            ReglaFraude regla = reglaFraudeMapper.toModel(reglaDTO);
            ReglaFraude reglaCreada = reglaFraudeService.crearRegla(regla);
            return ResponseEntity.ok(reglaFraudeMapper.toDTO(reglaCreada));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Error de validación: " + e.getMessage());
        } catch (RuntimeException e) {
            throw new RuntimeException("Error al crear la regla: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReglaFraudeDTO> actualizarReglaFraude(
            @PathVariable Integer id,
            @Valid @RequestBody ReglaFraudeDTO reglaDTO) {
        try {
            ReglaFraude regla = reglaFraudeMapper.toModel(reglaDTO);
            ReglaFraude reglaActualizada = reglaFraudeService.actualizarRegla(id, regla);
            return ResponseEntity.ok(reglaFraudeMapper.toDTO(reglaActualizada));
        } catch (NotFoundException e) {
            throw e;
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Error de validación: " + e.getMessage());
        } catch (RuntimeException e) {
            throw new RuntimeException("Error al actualizar la regla: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> desactivarReglaFraude(@PathVariable Integer id) {
        try {
            reglaFraudeService.desactivarRegla(id);
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Regla desactivada exitosamente");
            return ResponseEntity.ok(response);
        } catch (NotFoundException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new RuntimeException("Error al desactivar la regla: " + e.getMessage());
        }
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFoundException(NotFoundException e) {
        Map<String, String> response = new HashMap<>();
        response.put("error", e.getMessage());
        return ResponseEntity.status(404).body(response);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException e) {
        Map<String, String> response = new HashMap<>();
        response.put("error", e.getMessage());
        return ResponseEntity.status(400).body(response);
    }
} 