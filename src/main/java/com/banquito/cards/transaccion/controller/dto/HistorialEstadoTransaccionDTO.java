package com.banquito.cards.transaccion.controller.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Data
public class HistorialEstadoTransaccionDTO {
    private Integer codHistorialEstado;

    @NotBlank(message = "El código de transacción es requerido")
    private String codTransaccion;

    @NotBlank(message = "El estado es requerido")
    @Pattern(regexp = "PEN|APR|REC|REV|PRO", message = "Estado inválido")
    private String estado;

    @NotNull(message = "La fecha de cambio de estado es requerida")
    @PastOrPresent(message = "La fecha de cambio de estado no puede ser futura")
    private LocalDateTime fechaEstadoCambio;

    @Size(max = 500, message = "El detalle no puede exceder los 500 caracteres")
    private String detalle;

    private TransaccionDTO transaccion;
} 