package com.banquito.cards.transaccion.controller.dto;

import lombok.Data;
import lombok.Builder;
import java.time.LocalDateTime;

@Data
@Builder
public class HistorialEstadoTransaccionDTO {
    private Integer codigo;
    private Integer codigoTransaccion;
    private String estado;
    private LocalDateTime fechaEstadoCambio;
    private String detalle;
    private String nombreBanco;
    private String numeroTarjeta;
} 