package com.banquito.cards.fraude.controller.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ReglaFraudeDTO {
    private Integer codRegla;

    @NotBlank(message = "El nombre de la regla es requerido")
    @Size(min = 5, max = 50, message = "El nombre de la regla debe tener entre 5 y 50 caracteres")
    private String nombreRegla;

    @NotBlank(message = "La descripción es requerida")
    @Size(min = 10, max = 500, message = "La descripción debe tener entre 10 y 500 caracteres")
    private String descripcion;

    @NotBlank(message = "El tipo de regla es requerido")
    @Pattern(regexp = "TRX|MNT|GEO|COM|HOR", message = "Tipo de regla inválido")
    private String tipoRegla;

    @DecimalMin(value = "1", message = "El límite de transacciones debe ser mayor a 0")
    @DecimalMax(value = "999999999", message = "El límite de transacciones excede el máximo permitido")
    private BigDecimal limiteTransacciones;

    @Pattern(regexp = "MIN|HOR|DIA", message = "Periodo de tiempo inválido")
    private String periodoTiempo;

    @DecimalMin(value = "0.01", message = "El límite de monto total debe ser mayor a 0")
    @DecimalMax(value = "999999999999999999.99", message = "El límite de monto total excede el máximo permitido")
    private BigDecimal limiteMontoTotal;

    @Size(max = 1000, message = "La lista de países permitidos no puede exceder los 1000 caracteres")
    private String paisesPermitidos;

    @Size(max = 1000, message = "La lista de comercios excluidos no puede exceder los 1000 caracteres")
    private String comerciosExcluidos;

    private LocalDateTime horaInicio;
    private LocalDateTime horaFin;

    @DecimalMin(value = "0.00", message = "El puntaje de riesgo no puede ser negativo")
    @DecimalMax(value = "100.00", message = "El puntaje de riesgo no puede ser mayor a 100")
    private BigDecimal puntajeRiesgo;

    @NotBlank(message = "El nivel de riesgo es requerido")
    @Pattern(regexp = "BAJ|MED|ALT", message = "Nivel de riesgo inválido")
    private String nivelRiesgo;

    @NotBlank(message = "El estado es requerido")
    @Pattern(regexp = "ACT|INA", message = "Estado inválido")
    private String estado;

    @NotNull(message = "La prioridad es requerida")
    @Min(value = 1, message = "La prioridad debe ser mayor a 0")
    @Max(value = 999, message = "La prioridad no puede ser mayor a 999")
    private Integer prioridad;

    private LocalDateTime fechaCreacion;

    private LocalDateTime fechaActualizacion;

    @Size(max = 50, message = "El usuario de creación no puede exceder los 50 caracteres")
    private String usuarioCreacion;

    @Size(max = 50, message = "El usuario de actualización no puede exceder los 50 caracteres")
    private String usuarioActualizacion;
} 