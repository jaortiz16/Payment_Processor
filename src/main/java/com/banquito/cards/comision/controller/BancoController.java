package com.banquito.cards.comision.controller;

import com.banquito.cards.comision.controller.dto.BancoDTO;
import com.banquito.cards.comision.controller.mapper.BancoMapper;
import com.banquito.cards.comision.model.Banco;
import com.banquito.cards.comision.service.BancoService;
import com.banquito.cards.exception.NotFoundException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/bancos")
public class BancoController {

    private static final String ENTITY_NAME = "Banco";
    private final BancoService bancoService;
    private final BancoMapper bancoMapper;

    public BancoController(BancoService bancoService, BancoMapper bancoMapper) {
        this.bancoService = bancoService;
        this.bancoMapper = bancoMapper;
    }

    @GetMapping("/bancos-activos")
    public ResponseEntity<List<BancoDTO>> obtenerBancosActivos() {
        try {
            List<Banco> bancos = bancoService.obtenerBancosActivos();
            List<BancoDTO> bancosDTO = bancos.stream()
                    .map(bancoMapper::toDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(bancosDTO);
        } catch (RuntimeException e) {
            throw new RuntimeException("Error al obtener bancos activos: " + e.getMessage());
        }
    }

    @GetMapping("/buscar-por-razon-social")
    public ResponseEntity<List<BancoDTO>> obtenerBancosPorRazonSocialYEstado(
            @RequestParam String razonSocial,
            @RequestParam(required = false, defaultValue = "ACT") String estado) {
        try {
            List<Banco> bancos = bancoService.obtenerBancosPorRazonSocialYEstado(razonSocial, estado);
            List<BancoDTO> bancosDTO = bancos.stream()
                    .map(bancoMapper::toDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(bancosDTO);
        } catch (RuntimeException e) {
            throw new NotFoundException(razonSocial, ENTITY_NAME);
        }
    }

    @GetMapping("/buscar-nombre")
    public ResponseEntity<List<BancoDTO>> obtenerBancosPorNombreYEstado(
            @RequestParam String nombreComercial,
            @RequestParam(required = false, defaultValue = "ACT") String estado) {
        try {
            List<Banco> bancos = bancoService.obtenerBancosPorNombreYEstado(nombreComercial, estado);
            List<BancoDTO> bancosDTO = bancos.stream()
                    .map(bancoMapper::toDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(bancosDTO);
        } catch (RuntimeException e) {
            throw new NotFoundException(nombreComercial, ENTITY_NAME);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<BancoDTO> obtenerBancoPorId(@PathVariable Integer id) {
        try {
            Banco banco = bancoService.obtenerBancoPorId(id);
            return ResponseEntity.ok(bancoMapper.toDTO(banco));
        } catch (RuntimeException e) {
            throw new NotFoundException(id.toString(), ENTITY_NAME);
        }
    }

    @GetMapping("/ruc/{ruc}")
    public ResponseEntity<BancoDTO> obtenerBancoPorRuc(@PathVariable String ruc) {
        try {
            Banco banco = bancoService.obtenerBancoPorRuc(ruc);
            return ResponseEntity.ok(bancoMapper.toDTO(banco));
        } catch (RuntimeException e) {
            throw new NotFoundException(ruc, ENTITY_NAME);
        }
    }

    @PostMapping("/agregar-banco")
    public ResponseEntity<BancoDTO> crearBanco(@RequestBody BancoDTO bancoDTO) {
        try {
            Banco banco = bancoMapper.toModel(bancoDTO);
            Banco bancoCreado = bancoService.crearBanco(banco);
            return ResponseEntity.ok(bancoMapper.toDTO(bancoCreado));
        } catch (RuntimeException e) {
            throw new RuntimeException("Error al crear el banco: " + e.getMessage());
        }
    }

    @PutMapping("/actualizar-banco/{id}")
    public ResponseEntity<BancoDTO> actualizarBanco(
            @PathVariable Integer id, 
            @RequestBody BancoDTO bancoDTO) {
        try {
            Banco banco = bancoMapper.toModel(bancoDTO);
            Banco bancoActualizado = bancoService.actualizarBanco(id, banco);
            return ResponseEntity.ok(bancoMapper.toDTO(bancoActualizado));
        } catch (RuntimeException e) {
            throw new RuntimeException("Error al actualizar el banco: " + e.getMessage());
        }
    }

    @DeleteMapping("/inactivar-banco/{id}")
    public ResponseEntity<Map<String, String>> inactivarBanco(@PathVariable Integer id) {
        try {
            bancoService.inactivarBanco(id);
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Banco inactivado exitosamente");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            throw new RuntimeException("Error al inactivar el banco: " + e.getMessage());
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