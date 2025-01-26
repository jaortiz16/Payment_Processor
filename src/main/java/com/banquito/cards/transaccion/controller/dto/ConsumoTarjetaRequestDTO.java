package com.banquito.cards.transaccion.controller.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class ConsumoTarjetaRequestDTO {
    
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

    @NotNull(message = "El número de tarjeta es requerido")
    @Size(min = 16, max = 128, message = "El número de tarjeta debe tener entre 16 y 128 caracteres")
    private String numeroTarjeta;

    @NotNull(message = "El CVV es requerido")
    @Size(min = 3, max = 128, message = "El CVV debe tener entre 3 y 128 caracteres")
    private String cvv;

    @NotNull(message = "El país es requerido")
    @Size(min = 2, max = 2, message = "El código de país debe tener 2 caracteres")
    private String pais;
} 