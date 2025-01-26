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

    public TransaccionResponseDTO(String mensaje) {
        this.mensaje = mensaje;
    }

    public TransaccionResponseDTO(String mensaje, boolean isError) {
        if (isError) {
            this.error = mensaje;
        } else {
            this.mensaje = mensaje;
        }
    }

    public TransaccionResponseDTO(String mensaje, String estado, BigDecimal monto) {
        this.mensaje = mensaje;
        this.estado = estado;
        this.monto = monto;
    }
} 
