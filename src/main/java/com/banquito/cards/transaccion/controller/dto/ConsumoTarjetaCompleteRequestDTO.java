package com.banquito.cards.transaccion.controller.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsumoTarjetaCompleteRequestDTO {
    
    @NotNull(message = "El código del banco es requerido")
    private Integer codigoBanco;

    @NotNull(message = "El monto es requerido")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor a 0")
    @DecimalMax(value = "999999999999999999.99", message = "El monto excede el límite permitido")
    private BigDecimal monto;

    @NotNull(message = "La modalidad es requerida")
    @Pattern(regexp = "SIM|REC", message = "La modalidad debe ser SIM o REC")
    private String modalidad;

    @NotNull(message = "El código de moneda es requerido")
    @Size(min = 3, max = 3, message = "El código de moneda debe tener 3 caracteres")
    private String codigoMoneda;

    @NotNull(message = "La marca es requerida")
    @Size(min = 2, max = 4, message = "La marca debe tener entre 2 y 4 caracteres")
    private String marca;

    @NotNull(message = "La fecha de expiración es requerida")
    @Size(min = 4, max = 64, message = "La fecha de expiración debe tener entre 4 y 64 caracteres")
    private String fechaExpiracionTarjeta;

    @NotNull(message = "El nombre del titular es requerido")
    @Size(min = 5, max = 128, message = "El nombre del titular debe tener entre 5 y 128 caracteres")
    private String nombreTarjeta;

    @NotNull(message = "El número de tarjeta es requerido")
    @Size(min = 16, max = 128, message = "El número de tarjeta debe tener entre 16 y 128 caracteres")
    private String numeroTarjeta;

    @NotNull(message = "La dirección es requerida")
    @Size(min = 10, max = 256, message = "La dirección debe tener entre 10 y 256 caracteres")
    private String direccionTarjeta;

    @NotNull(message = "El CVV es requerido")
    @Size(min = 3, max = 128, message = "El CVV debe tener entre 3 y 128 caracteres")
    private String cvv;

    @NotNull(message = "El país es requerido")
    @Size(min = 2, max = 2, message = "El código de país debe tener 2 caracteres")
    private String pais;

    @Future(message = "La fecha de ejecución debe ser futura")
    private LocalDateTime fechaEjecucionRecurrencia;

    @Future(message = "La fecha de fin debe ser futura")
    private LocalDateTime fechaFinRecurrencia;

    @Size(min = 8, max = 20, message = "El número de cuenta debe tener entre 8 y 20 caracteres")
    private String numeroCuenta;

    @Min(value = 1, message = "El número de cuotas debe ser mayor a 0")
    @Max(value = 48, message = "El número de cuotas no puede exceder 48")
    private Integer cuotas;

    private Boolean interesDiferido;

    @Size(max = 128, message = "El beneficiario no puede exceder los 128 caracteres")
    private String beneficiario;

    @Size(max = 200, message = "La descripción no puede exceder los 200 caracteres")
    private String descripcion;
} 