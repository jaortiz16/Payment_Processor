package com.banquito.cards.transaccion.controller.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsumoTarjetaRequestDTO {
    
    @NotNull(message = "El número de tarjeta es requerido")
    @Size(min = 16, max = 128, message = "El número de tarjeta debe tener entre 16 y 128 caracteres")
    private String numeroTarjeta;

    @NotNull(message = "El CVV es requerido")
    @Size(min = 3, max = 128, message = "El CVV debe tener entre 3 y 128 caracteres")
    private String cvv;

    @NotNull(message = "La fecha de caducidad es requerida")
    private String fechaCaducidad;

    @NotNull(message = "El valor es requerido")
    @DecimalMin(value = "0.01", message = "El valor debe ser mayor a 0")
    @DecimalMax(value = "999999999999999999.99", message = "El valor excede el límite permitido")
    private BigDecimal valor;

    @Size(max = 200, message = "La descripción no puede exceder los 200 caracteres")
    private String descripcion;

    @Size(max = 128, message = "El beneficiario no puede exceder los 128 caracteres")
    private String beneficiario;

    @Size(min = 8, max = 20, message = "El número de cuenta debe tener entre 8 y 20 caracteres")
    private String numeroCuenta;

    private Boolean esDiferido;

    @Min(value = 0, message = "El número de cuotas no puede ser negativo")
    @Max(value = 48, message = "El número de cuotas no puede exceder 48")
    private Integer cuotas;

    @Data
    @NoArgsConstructor
    public static class DetalleComision {
        private String referencia;
        private BigDecimal comision;
        private String numeroCuenta;
    }

    @Data
    @NoArgsConstructor
    public static class Detalle {
        private DetalleComision gtw;
        private DetalleComision processor;
    }

    private Detalle detalle;
} 