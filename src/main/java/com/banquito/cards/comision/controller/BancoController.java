package com.banquito.cards.comision.controller;

import com.banquito.cards.comision.controller.dto.BancoDTO;
import com.banquito.cards.comision.controller.mapper.BancoMapper;
import com.banquito.cards.comision.model.Banco;
import com.banquito.cards.comision.service.BancoService;
import com.banquito.cards.exception.NotFoundException;
import com.banquito.cards.exception.BusinessException;

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

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Tag(name = "Bancos", description = "API para la gestión de bancos y sus comisiones")
@RestController
@RequestMapping("/v1/bancos")
public class BancoController {

    private static final String ENTITY_NAME = "Banco";
    private final BancoService bancoService;
    private final BancoMapper bancoMapper;

    public BancoController(BancoService bancoService, BancoMapper bancoMapper) {
        this.bancoService = bancoService;
        this.bancoMapper = bancoMapper;
    }

    @Operation(summary = "Listar bancos", description = "Retorna todos los bancos con opciones de filtrado y ordenamiento")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de bancos obtenida exitosamente",
                    content = {@Content(schema = @Schema(implementation = BancoDTO.class))}),
        @ApiResponse(responseCode = "400", description = "Parámetros de filtrado inválidos")
    })
    @GetMapping
    public ResponseEntity<List<BancoDTO>> listarBancos(
            @Parameter(description = "Razón social del banco") 
            @RequestParam(required = false) String razonSocial,
            @Parameter(description = "Nombre comercial del banco") 
            @RequestParam(required = false) String nombreComercial,
            @Parameter(description = "Estado del banco (ACT o INA)") 
            @RequestParam(required = false, defaultValue = "ACT") String estado,
            @Parameter(description = "Campo por el cual ordenar (razonSocial, nombreComercial, fechaCreacion)") 
            @RequestParam(required = false, defaultValue = "codigo") String sortBy,
            @Parameter(description = "Dirección del ordenamiento (asc o desc)") 
            @RequestParam(required = false, defaultValue = "asc") String sortDir) {
        try {
            List<Banco> bancos;
            if (razonSocial != null) {
                bancos = bancoService.obtenerBancosPorRazonSocialYEstado(razonSocial, estado);
            } else if (nombreComercial != null) {
                bancos = bancoService.obtenerBancosPorNombreYEstado(nombreComercial, estado);
            } else {
                bancos = bancoService.obtenerTodosLosBancos(sortBy, sortDir);
            }
            
            List<BancoDTO> bancosDTO = bancos.stream()
                    .map(bancoMapper::toDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(bancosDTO);
        } catch (RuntimeException e) {
            throw new BusinessException("listar bancos", ENTITY_NAME, e.getMessage());
        }
    }

    @Operation(summary = "Obtener banco por ID", description = "Retorna un banco específico basado en su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Banco encontrado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Banco no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<BancoDTO> obtenerBanco(
            @Parameter(description = "ID del banco") 
            @PathVariable Integer id) {
        try {
            Banco banco = bancoService.obtenerBancoPorId(id);
            return ResponseEntity.ok(bancoMapper.toDTO(banco));
        } catch (RuntimeException e) {
            throw new NotFoundException(id.toString(), ENTITY_NAME);
        }
    }

    @Operation(summary = "Crear nuevo banco", description = "Registra un nuevo banco en el sistema")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Banco creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Error en los datos del banco")
    })
    @PostMapping
    public ResponseEntity<BancoDTO> crearBanco(
            @Parameter(description = "Datos del banco") 
            @Valid @RequestBody BancoDTO bancoDTO) {
        try {
            Banco banco = bancoMapper.toModel(bancoDTO);
            Banco bancoCreado = bancoService.crearBanco(banco);
            return ResponseEntity.ok(bancoMapper.toDTO(bancoCreado));
        } catch (RuntimeException e) {
            throw new BusinessException(bancoDTO.getRuc(), ENTITY_NAME, "crear");
        }
    }

    @Operation(summary = "Actualizar banco", description = "Actualiza un banco existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Banco actualizado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Banco no encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<BancoDTO> actualizarBanco(
            @Parameter(description = "ID del banco") 
            @PathVariable Integer id,
            @Parameter(description = "Datos actualizados del banco") 
            @Valid @RequestBody BancoDTO bancoDTO) {
        try {
            Banco banco = bancoMapper.toModel(bancoDTO);
            Banco bancoActualizado = bancoService.actualizarBanco(id, banco);
            return ResponseEntity.ok(bancoMapper.toDTO(bancoActualizado));
        } catch (RuntimeException e) {
            throw new BusinessException(id.toString(), ENTITY_NAME, "actualizar");
        }
    }

    @Operation(summary = "Inactivar banco", description = "Cambia el estado de un banco a inactivo")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Banco inactivado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Banco no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> inactivarBanco(
            @Parameter(description = "ID del banco") 
            @PathVariable Integer id) {
        try {
            bancoService.inactivarBanco(id);
            return ResponseEntity.ok("Banco inactivado exitosamente");
        } catch (RuntimeException e) {
            throw new BusinessException(id.toString(), ENTITY_NAME, "inactivar");
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