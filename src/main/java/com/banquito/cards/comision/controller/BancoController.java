package com.banquito.cards.comision.controller;

import com.banquito.cards.comision.controller.dto.BancoDTO;
import com.banquito.cards.comision.controller.mapper.BancoMapper;
import com.banquito.cards.comision.model.Banco;
import com.banquito.cards.comision.service.BancoService;
import com.banquito.cards.exception.NotFoundException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@Slf4j
@Tag(name = "Bancos", description = "API para la gestión de bancos y sus comisiones")
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

    @Operation(summary = "Obtener bancos activos", 
               description = "Retorna la lista de todos los bancos que están activos en el sistema")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de bancos obtenida exitosamente",
                    content = {@Content(schema = @Schema(implementation = BancoDTO.class))}),
        @ApiResponse(responseCode = "400", description = "Error al obtener los bancos")
    })
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

    @Operation(summary = "Buscar bancos por razón social", 
               description = "Retorna los bancos que coincidan con la razón social y estado especificados")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Búsqueda realizada exitosamente"),
        @ApiResponse(responseCode = "404", description = "No se encontraron bancos")
    })
    @GetMapping("/buscar-por-razon-social")
    public ResponseEntity<List<BancoDTO>> obtenerBancosPorRazonSocialYEstado(
            @Parameter(description = "Razón social del banco") 
            @RequestParam String razonSocial,
            @Parameter(description = "Estado del banco (por defecto: ACT)") 
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

    @Operation(summary = "Buscar bancos por nombre comercial", 
               description = "Retorna los bancos que coincidan con el nombre comercial y estado especificados")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Búsqueda realizada exitosamente"),
        @ApiResponse(responseCode = "404", description = "No se encontraron bancos")
    })
    @GetMapping("/buscar-nombre")
    public ResponseEntity<List<BancoDTO>> obtenerBancosPorNombreYEstado(
            @Parameter(description = "Nombre comercial del banco") 
            @RequestParam String nombreComercial,
            @Parameter(description = "Estado del banco (por defecto: ACT)") 
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

    @Operation(summary = "Obtener banco por ID", 
               description = "Retorna un banco específico basado en su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Banco encontrado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Banco no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<BancoDTO> obtenerBancoPorId(
            @Parameter(description = "ID del banco") 
            @PathVariable Integer id) {
        try {
            Banco banco = bancoService.obtenerBancoPorId(id);
            return ResponseEntity.ok(bancoMapper.toDTO(banco));
        } catch (RuntimeException e) {
            throw new NotFoundException(id.toString(), ENTITY_NAME);
        }
    }

    @Operation(summary = "Obtener banco por RUC", 
               description = "Retorna un banco específico basado en su número de RUC")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Banco encontrado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Banco no encontrado")
    })
    @GetMapping("/ruc/{ruc}")
    public ResponseEntity<BancoDTO> obtenerBancoPorRuc(
            @Parameter(description = "Número de RUC del banco") 
            @PathVariable String ruc) {
        try {
            Banco banco = bancoService.obtenerBancoPorRuc(ruc);
            return ResponseEntity.ok(bancoMapper.toDTO(banco));
        } catch (RuntimeException e) {
            throw new NotFoundException(ruc, ENTITY_NAME);
        }
    }

    @Operation(summary = "Crear nuevo banco", 
               description = "Registra un nuevo banco en el sistema")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Banco creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Error en los datos del banco")
    })
    @PostMapping("/agregar-banco")
    public ResponseEntity<BancoDTO> crearBanco(
            @Parameter(description = "Datos del banco a crear") 
            @RequestBody BancoDTO bancoDTO) {
        try {
            Banco banco = bancoMapper.toModel(bancoDTO);
            Banco bancoCreado = bancoService.crearBanco(banco);
            return ResponseEntity.ok(bancoMapper.toDTO(bancoCreado));
        } catch (RuntimeException e) {
            throw new RuntimeException("Error al crear el banco: " + e.getMessage());
        }
    }

    @Operation(summary = "Actualizar banco", 
               description = "Actualiza la información de un banco existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Banco actualizado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Error en los datos del banco"),
        @ApiResponse(responseCode = "404", description = "Banco no encontrado")
    })
    @PutMapping("/actualizar-banco/{id}")
    public ResponseEntity<BancoDTO> actualizarBanco(
            @Parameter(description = "ID del banco a actualizar") 
            @PathVariable Integer id, 
            @Parameter(description = "Nuevos datos del banco") 
            @RequestBody BancoDTO bancoDTO) {
        try {
            Banco banco = bancoMapper.toModel(bancoDTO);
            Banco bancoActualizado = bancoService.actualizarBanco(id, banco);
            return ResponseEntity.ok(bancoMapper.toDTO(bancoActualizado));
        } catch (RuntimeException e) {
            throw new RuntimeException("Error al actualizar el banco: " + e.getMessage());
        }
    }

    @Operation(summary = "Inactivar banco", 
               description = "Cambia el estado de un banco a inactivo")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Banco inactivado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Error al inactivar el banco"),
        @ApiResponse(responseCode = "404", description = "Banco no encontrado")
    })
    @DeleteMapping("/inactivar-banco/{id}")
    public ResponseEntity<Map<String, String>> inactivarBanco(
            @Parameter(description = "ID del banco a inactivar") 
            @PathVariable Integer id) {
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