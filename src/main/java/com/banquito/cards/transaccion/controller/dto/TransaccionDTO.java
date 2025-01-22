package com.banquito.cards.transaccion.controller.dto;

import com.banquito.cards.comision.controller.dto.BancoDTO;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransaccionDTO {
    private Integer codigo;
    
    @NotNull(message = "El código del banco es requerido")
    private Integer codigoBanco;
    
    @NotBlank(message = "El número de tarjeta es requerido")
    @Size(min = 16, max = 19, message = "El número de tarjeta debe tener entre 16 y 19 caracteres")
    private String numeroTarjeta;
    
    @NotBlank(message = "La marca es requerida")
    @Size(min = 2, max = 4, message = "La marca debe tener entre 2 y 4 caracteres")
    private String marca;
    
    @NotBlank(message = "La modalidad es requerida")
    @Pattern(regexp = "SIM|REC", message = "La modalidad debe ser SIM o REC")
    private String modalidad;
    
    @NotBlank(message = "El código de moneda es requerido")
    @Size(min = 3, max = 3, message = "El código de moneda debe tener 3 caracteres")
    private String codigoMoneda;
    
    @NotNull(message = "El monto es requerido")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor a 0")
    @DecimalMax(value = "999999999999999999.99", message = "El monto excede el límite permitido")
    private BigDecimal monto;
    
    @NotBlank(message = "El estado es requerido")
    @Pattern(regexp = "PEN|APR|REC|REV|PRO", message = "Estado inválido")
    private String estado;
    
    @Size(max = 50, message = "El detalle no puede exceder los 50 caracteres")
    private String detalle;
    
    @Size(min = 32, max = 64, message = "El código único de transacción debe tener entre 32 y 64 caracteres")
    private String codigoUnicoTransaccion;
    
    @NotBlank(message = "El país es requerido")
    @Size(min = 2, max = 2, message = "El código de país debe tener 2 caracteres")
    private String pais;
    
    @NotBlank(message = "La fecha de expiración es requerida")
    @Size(min = 4, max = 64, message = "La fecha de expiración debe tener entre 4 y 64 caracteres")
    private String fechaExpiracionTarjeta;
    
    @NotBlank(message = "El nombre de la tarjeta es requerido")
    @Size(min = 5, max = 128, message = "El nombre de la tarjeta debe tener entre 5 y 128 caracteres")
    private String nombreTarjeta;
    
    @NotBlank(message = "La dirección es requerida")
    @Size(min = 10, max = 256, message = "La dirección debe tener entre 10 y 256 caracteres")
    private String direccionTarjeta;
    
    @NotBlank(message = "El CVV es requerido")
    @Size(min = 3, max = 4, message = "El CVV debe tener entre 3 y 4 caracteres")
    private String cvv;
    
    @NotNull(message = "La fecha de creación es requerida")
    @PastOrPresent(message = "La fecha de creación no puede ser futura")
    private LocalDateTime fechaCreacion;
    
    @Future(message = "La fecha de ejecución recurrente debe ser futura")
    private LocalDateTime fechaEjecucionRecurrencia;
    
    @Future(message = "La fecha de fin de recurrencia debe ser futura")
    private LocalDateTime fechaFinRecurrencia;
    
    @Min(value = 1, message = "El número de cuotas debe ser mayor a 0")
    @Max(value = 48, message = "El número de cuotas no puede exceder 48")
    private Integer cuotas;
    
    @Pattern(regexp = "^\\d+(\\.\\d{1,2})?$", message = "El formato de la comisión gateway es inválido")
    private String gtwComision;
    
    @Size(min = 8, max = 20, message = "La cuenta gateway debe tener entre 8 y 20 caracteres")
    private String gtwCuenta;
    
    @Size(min = 8, max = 20, message = "El número de cuenta debe tener entre 8 y 20 caracteres")
    private String numeroCuenta;
    
    private Boolean interesDiferido;
    
    @Size(max = 128, message = "El beneficiario no puede exceder los 128 caracteres")
    private String beneficiario;
    
    private BancoDTO banco;
} 