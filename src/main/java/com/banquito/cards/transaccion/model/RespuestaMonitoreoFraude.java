package com.banquito.cards.transaccion.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RespuestaMonitoreoFraude {
    private boolean success;
    private String estado;
    private String message;
    private String codigoUnicoTransaccion;
    private String nivelRiesgo;
    private String detalle;
} 