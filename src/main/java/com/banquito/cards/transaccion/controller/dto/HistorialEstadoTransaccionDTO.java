package com.banquito.cards.transaccion.controller.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Data
public class HistorialEstadoTransaccionDTO {
    private Integer codHistorialEstado;
    private String codTransaccion;
    private String estado;
    private LocalDateTime fechaEstadoCambio;
    private String detalle;
    private TransaccionDTO transaccion;
} 