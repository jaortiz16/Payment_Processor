package com.banquito.cards.transaccion.controller.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class HistorialEstadoTransaccionDTO {
    
    private Integer codHistorialEstado;

    @NotNull(message = "El código de transacción es requerido")
    private Integer codigoTransaccion;

    @NotNull(message = "El estado es requerido")
    @Pattern(regexp = "PEN|APR|REC|REV|PRO", message = "El estado debe ser PEN, APR, REC, REV o PRO")
    private String estado;

    @NotNull(message = "La fecha de cambio de estado es requerida")
    @PastOrPresent(message = "La fecha de cambio de estado no puede ser futura")
    private LocalDateTime fechaEstadoCambio;

    @Size(max = 200, message = "El detalle no puede exceder los 200 caracteres")
    private String detalle;

    private TransaccionDTO transaccion;
} 