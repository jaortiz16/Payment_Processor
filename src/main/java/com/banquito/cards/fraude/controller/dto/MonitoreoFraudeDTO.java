package com.banquito.cards.fraude.controller.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MonitoreoFraudeDTO {
    @NotBlank(message = "El código es requerido")
    private String codigo;

    @NotNull(message = "El código de regla es requerido")
    private Integer codRegla;

    @NotNull(message = "El código de transacción es requerido")
    private Integer codTransaccion;

    @NotBlank(message = "El nivel de riesgo es requerido")
    @Pattern(regexp = "BAJ|MED|ALT", message = "Nivel de riesgo inválido")
    private String nivelRiesgo;

    @NotNull(message = "El puntaje de riesgo es requerido")
    @Min(value = 0, message = "El puntaje de riesgo no puede ser negativo")
    @Max(value = 100, message = "El puntaje de riesgo no puede ser mayor a 100")
    private Integer puntajeRiesgo;

    @NotBlank(message = "El estado es requerido")
    @Pattern(regexp = "PEN|PRO|REC|APR|REV", message = "Estado inválido")
    private String estado;

    @Size(max = 500, message = "El detalle no puede exceder los 500 caracteres")
    private String detalle;

    @Size(max = 128, message = "La acción tomada no puede exceder los 128 caracteres")
    private String accionTomada;

    @NotNull(message = "El campo requiere verificación adicional es requerido")
    private Boolean requiereVerificacionAdicional;

    @Size(max = 128, message = "El motivo de verificación no puede exceder los 128 caracteres")
    private String motivoVerificacion;

    @NotNull(message = "La fecha de detección es requerida")
    @PastOrPresent(message = "La fecha de detección no puede ser futura")
    private LocalDateTime fechaDeteccion;

    @PastOrPresent(message = "La fecha de procesamiento no puede ser futura")
    private LocalDateTime fechaProcesamiento;

    @NotBlank(message = "El código único de transacción es requerido")
    @Size(min = 32, max = 64, message = "El código único de transacción debe tener entre 32 y 64 caracteres")
    private String codigoUnicoTransaccion;

    @Size(max = 64, message = "El usuario de procesamiento no puede exceder los 64 caracteres")
    private String usuarioProcesamiento;

    @Size(max = 64, message = "La IP de origen no puede exceder los 64 caracteres")
    private String ipOrigen;

    @Size(max = 128, message = "La ubicación geográfica no puede exceder los 128 caracteres")
    private String ubicacionGeografica;
} 