package com.banquito.cards.fraude.controller.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class ReglaFraudeDTO {
    private Integer codRegla;
    
    @NotNull(message = "El nombre de la regla es requerido")
    @Size(min = 5, max = 50, message = "El nombre debe tener entre 5 y 50 caracteres")
    private String nombreRegla;
    
    @NotNull(message = "La descripción es requerida")
    @Size(min = 10, max = 500, message = "La descripción debe tener entre 10 y 500 caracteres")
    private String descripcion;
    
    @NotNull(message = "El tipo de regla es requerido")
    @Pattern(regexp = "TRX|MNT|GEO|COM|HOR", message = "Tipo de regla inválido")
    private String tipoRegla;
    
    @Min(value = 1, message = "El límite de transacciones debe ser mayor a 0")
    @Max(value = 999999999, message = "El límite de transacciones no puede exceder 999999999")
    private BigDecimal limiteTransacciones;
    
    @NotNull(message = "El periodo de tiempo es requerido")
    @Pattern(regexp = "MIN|HOR|DIA", message = "Periodo de tiempo inválido")
    private String periodoTiempo;
    
    @DecimalMin(value = "0.01", message = "El límite de monto total debe ser mayor a 0")
    @DecimalMax(value = "999999999999999999.99", message = "El límite de monto total no puede exceder 999999999999999999.99")
    private BigDecimal limiteMontoTotal;
    
    private String paisesPermitidos;
    private String comerciosExcluidos;
    private LocalDateTime horaInicio;
    private LocalDateTime horaFin;
    
    @NotNull(message = "El puntaje de riesgo es requerido")
    @DecimalMin(value = "0.01", message = "El puntaje de riesgo debe ser mayor a 0")
    @DecimalMax(value = "100.00", message = "El puntaje de riesgo no puede exceder 100")
    private BigDecimal puntajeRiesgo;
    
    @NotNull(message = "El nivel de riesgo es requerido")
    @Pattern(regexp = "BAJ|MED|ALT", message = "Nivel de riesgo inválido")
    private String nivelRiesgo;
    
    @NotNull(message = "El estado es requerido")
    @Pattern(regexp = "ACT|INA", message = "Estado inválido")
    private String estado;
    
    @NotNull(message = "La prioridad es requerida")
    @Min(value = 1, message = "La prioridad debe ser mayor a 0")
    @Max(value = 99, message = "La prioridad no puede exceder 99")
    private Integer prioridad;
    
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    private String usuarioCreacion;
    private String usuarioActualizacion;
} 