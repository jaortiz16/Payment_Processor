package com.banquito.cards.comision.controller.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class ComisionSegmentoDTO {
    private Integer codComision;
    
    @NotNull(message = "El rango inicial de transacciones es requerido")
    @DecimalMin(value = "0.0", message = "El rango inicial no puede ser negativo")
    private BigDecimal transaccionesDesde;

    @NotNull(message = "El rango final de transacciones es requerido")
    @DecimalMin(value = "0.0", message = "El rango final no puede ser negativo")
    private BigDecimal transaccionesHasta;

    @NotNull(message = "El monto es requerido")
    @DecimalMin(value = "0.0", message = "El monto no puede ser negativo")
    @DecimalMax(value = "999.9999", message = "El monto excede el límite permitido")
    private BigDecimal monto;
} 