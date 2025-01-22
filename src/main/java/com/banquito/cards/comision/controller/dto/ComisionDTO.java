package com.banquito.cards.comision.controller.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class ComisionDTO {
    private Integer codigo;

    @NotBlank(message = "El tipo es requerido")
    @Pattern(regexp = "POR|FIJ", message = "El tipo debe ser POR o FIJ")
    private String tipo;

    @NotNull(message = "El monto base es requerido")
    @DecimalMin(value = "0.0", message = "El monto base no puede ser negativo")
    @DecimalMax(value = "999999999.9999", message = "El monto base excede el límite permitido")
    private BigDecimal montoBase;

    @NotNull(message = "El número de transacciones base es requerido")
    @Min(value = 0, message = "El número de transacciones base no puede ser negativo")
    private Integer transaccionesBase;

    @NotNull(message = "El campo maneja segmentos es requerido")
    private Boolean manejaSegmentos;
} 