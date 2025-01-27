package com.banquito.cards.transaccion.controller.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProcesamientoFraudeRequestDTO {
    
    @NotNull(message = "El código de transacción es requerido")
    private Integer codigoTransaccion;

    @NotNull(message = "El código único de transacción es requerido")
    private String codigoUnicoTransaccion;
} 