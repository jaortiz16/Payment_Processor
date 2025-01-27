package com.banquito.cards.transaccion.controller.dto;

import java.math.BigDecimal;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TransaccionResponseDTO {
    private String mensaje;
    private String error;
    private String estado;
    private BigDecimal monto;
    private boolean esError;

    public TransaccionResponseDTO(String mensaje, String estado, BigDecimal monto) {
        this.mensaje = mensaje != null ? mensaje : "Transacción procesada";
        this.estado = estado != null ? estado : "PEN";
        this.monto = monto;
        this.error = null;
        this.esError = false;
    }

    public TransaccionResponseDTO(String error, boolean esError) {
        this.error = error != null ? error : "Error desconocido";
        this.esError = esError;
        this.mensaje = null;
        this.estado = "REC";
        this.monto = null;
    }
} 
