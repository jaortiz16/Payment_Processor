package com.banquito.cards.comision.controller;

import com.banquito.cards.comision.controller.dto.ComisionDTO;
import com.banquito.cards.comision.controller.mapper.ComisionMapper;
import com.banquito.cards.comision.model.Comision;
import com.banquito.cards.comision.model.ComisionSegmento;
import com.banquito.cards.comision.service.ComisionService;
import com.banquito.cards.exception.NotFoundException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

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

    @GetMapping("/tipo-comision/{tipo}")
    public ResponseEntity<List<ComisionDTO>> obtenerPorTipo(@PathVariable String tipo) {
        try {
            List<Comision> comisiones = comisionService.obtenerComisionesPorTipo(tipo);
            List<ComisionDTO> comisionesDTO = comisiones.stream()
                    .map(comisionMapper::toDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(comisionesDTO);
        } catch (RuntimeException e) {
            throw new NotFoundException(tipo, ENTITY_NAME);
        }
    }

    @GetMapping("/monto-comision")
    public ResponseEntity<List<ComisionDTO>> buscarPorMontoBase(
            @RequestParam BigDecimal montoMinimo,
            @RequestParam BigDecimal montoMaximo) {
        try {
            List<Comision> comisiones = comisionService.obtenerComisionesPorMontoBaseEntre(montoMinimo, montoMaximo);
            List<ComisionDTO> comisionesDTO = comisiones.stream()
                    .map(comisionMapper::toDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(comisionesDTO);
        } catch (RuntimeException e) {
            throw new NotFoundException("monto entre " + montoMinimo + " y " + montoMaximo, ENTITY_NAME);
        }
    }

    @GetMapping("/obtener-por-id/{id}")
    public ResponseEntity<ComisionDTO> obtenerPorId(@PathVariable Integer id) {
        try {
            Comision comision = comisionService.obtenerComisionPorId(id);
            return ResponseEntity.ok(comisionMapper.toDTO(comision));
        } catch (RuntimeException e) {
            throw new NotFoundException(id.toString(), ENTITY_NAME);
        }
    }

    @PostMapping("/crear-comision")
    public ResponseEntity<ComisionDTO> crearComision(@RequestBody ComisionDTO comisionDTO) {
        try {
            Comision comision = comisionMapper.toModel(comisionDTO);
            Comision comisionCreada = comisionService.crearComision(comision);
            return ResponseEntity.ok(comisionMapper.toDTO(comisionCreada));
        } catch (RuntimeException e) {
            throw new RuntimeException("Error al crear la comisión: " + e.getMessage());
        }
    }

    @PutMapping("/actualizar-comision/{id}")
    public ResponseEntity<ComisionDTO> actualizarComision(
            @PathVariable Integer id, 
            @RequestBody ComisionDTO comisionDTO) {
        try {
            Comision comision = comisionMapper.toModel(comisionDTO);
            Comision comisionActualizada = comisionService.actualizarComision(id, comision);
            return ResponseEntity.ok(comisionMapper.toDTO(comisionActualizada));
        } catch (RuntimeException e) {
            throw new RuntimeException("Error al actualizar la comisión: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/segmentos")
    public ResponseEntity<Map<String, String>> agregarSegmento(
            @PathVariable Integer id,
            @RequestBody ComisionSegmento segmento) {
        try {
            comisionService.agregarSegmento(id, segmento);
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Segmento agregado exitosamente");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            throw new RuntimeException("Error al agregar el segmento: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/calcular")
    public ResponseEntity<Map<String, BigDecimal>> calcularComision(
            @PathVariable Integer id,
            @RequestParam Integer numeroTransacciones,
            @RequestParam BigDecimal montoTransaccion) {
        try {
            BigDecimal comisionCalculada = comisionService.calcularComision(id, numeroTransacciones, montoTransaccion);
            Map<String, BigDecimal> response = new HashMap<>();
            response.put("comision", comisionCalculada);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            throw new RuntimeException("Error al calcular la comisión: " + e.getMessage());
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