package com.banquito.cards.fraude.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FraudeResponseDTO {
    private String mensaje;
    private String error;
    private BigDecimal puntajeRiesgo;
    private String nivelRiesgo;

    public FraudeResponseDTO(String mensaje) {
        this.mensaje = mensaje;
    }

    public FraudeResponseDTO(String error, boolean isError) {
        this.error = error;
    }

    public FraudeResponseDTO(BigDecimal puntajeRiesgo, String nivelRiesgo) {
        this.puntajeRiesgo = puntajeRiesgo;
        this.nivelRiesgo = nivelRiesgo;
    }
} 