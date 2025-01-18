package com.banquito.cards.transaccion.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcesamientoFraudeRequest {
    
    @NotNull
    @Size(min = 32, max = 64)
    private String codigoUnicoTransaccion;
    
    @NotNull
    @Size(min = 3, max = 10)
    private String decision;
    
    private String observacion;
} 