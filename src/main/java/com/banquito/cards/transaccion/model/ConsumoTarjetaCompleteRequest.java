package com.banquito.cards.transaccion.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsumoTarjetaCompleteRequest {
    
    @NotNull
    @Size(min = 16, max = 128)
    private String numeroTarjeta;
    
    @NotNull
    @Size(min = 3, max = 128)
    private String cvv;
    
    @NotNull
    @Size(min = 4, max = 64)
    private String fechaCaducidad;
    
    @NotNull
    @DecimalMin("0.01")
    @DecimalMax("999999999999999999.99")
    private BigDecimal valor;
    
    @Size(max = 500)
    private String descripcion;
    
    @Size(min = 8, max = 20)
    private String numeroCuenta;
    
    private Boolean esDiferido;
    
    @Min(1)
    @Max(48)
    private Integer cuotas;
    
    private Boolean interesDiferido;
    
    @Size(max = 128)
    private String beneficiario;
    
    @NotNull
    private DetalleComisiones detalle;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DetalleComisiones {
        private ComisionDetalle gtw;
        private ComisionDetalle processor;
        private ComisionDetalle marca;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ComisionDetalle {
        @NotNull
        @DecimalMin("0.00")
        @DecimalMax("100.00")
        private BigDecimal comision;
        
        @NotNull
        @Size(min = 8, max = 20)
        private String numeroCuenta;
    }
} 