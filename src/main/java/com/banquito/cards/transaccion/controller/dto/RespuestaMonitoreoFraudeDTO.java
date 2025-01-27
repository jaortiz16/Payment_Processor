package com.banquito.cards.transaccion.controller.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RespuestaMonitoreoFraudeDTO {
    
    @NotNull(message = "El estado es requerido")
    @Pattern(regexp = "PEN|APR|REC|REV|PRO", message = "El estado debe ser PEN, APR, REC, REV o PRO")
    private String estado;

    @NotNull(message = "El código de monitoreo es requerido")
    private String codigoMonitoreo;
} 