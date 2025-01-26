package com.banquito.cards.transaccion.controller.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RespuestaBancoDTO {
    
    @NotNull(message = "El código de autorización es requerido")
    @Size(min = 6, max = 10, message = "El código de autorización debe tener entre 6 y 10 caracteres")
    private String codigoAutorizacion;

    @NotNull(message = "El estado es requerido")
    @Size(min = 3, max = 3, message = "El estado debe tener 3 caracteres")
    private String estado;

    @Size(max = 200, message = "El detalle no puede exceder los 200 caracteres")
    private String detalle;
} 