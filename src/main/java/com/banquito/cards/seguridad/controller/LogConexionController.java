package com.banquito.cards.seguridad.controller;

import com.banquito.cards.seguridad.model.LogConexion;
import com.banquito.cards.seguridad.service.LogConexionService;

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

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Tag(name = "Logs de Conexión", description = "API para el registro y consulta de logs de conexión")
@RestController
@RequestMapping("/api/v1/logs-conexion")
public class LogConexionController {

    private final LogConexionService logConexionService;

    public LogConexionController(LogConexionService logConexionService) {
        this.logConexionService = logConexionService;
    }

    @Operation(summary = "Registrar nueva conexión", 
               description = "Registra un nuevo log de conexión en el sistema")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Log registrado exitosamente",
                    content = {@Content(schema = @Schema(implementation = LogConexion.class))}),
        @ApiResponse(responseCode = "400", description = "Error en los datos de la conexión")
    })
    @PostMapping
    public ResponseEntity<LogConexion> registrarConexion(
            @Parameter(description = "Marca o identificador del sistema") 
            @RequestParam String marca,
            @Parameter(description = "Código del banco que realiza la conexión") 
            @RequestParam Integer codBanco,
            @Parameter(description = "Dirección IP desde donde se origina la conexión") 
            @RequestParam String ipOrigen,
            @Parameter(description = "Operación realizada en la conexión") 
            @RequestParam String operacion,
            @Parameter(description = "Resultado de la operación") 
            @RequestParam String resultado) {
        log.info("Intentando registrar nueva conexión: marca={}, codBanco={}, ipOrigen={}, operacion={}, resultado={}",
                marca, codBanco, ipOrigen, operacion, resultado);
        try {
            LogConexion logConexion = logConexionService.registrarConexion(marca, codBanco, ipOrigen, operacion, resultado);
            log.info("Registro de conexión exitoso para marca={}, codBanco={}, ipOrigen={}", marca, codBanco, ipOrigen);
            return ResponseEntity.ok(logConexion);
        } catch (RuntimeException e) {
            log.error("Error al registrar la conexión: marca={}, codBanco={}, ipOrigen={}, operacion={}, resultado={}, error={}",
                    marca, codBanco, ipOrigen, operacion, resultado, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}