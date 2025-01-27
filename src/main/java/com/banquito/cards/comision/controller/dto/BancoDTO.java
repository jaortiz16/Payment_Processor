package com.banquito.cards.comision.controller.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class BancoDTO {
    private Integer codigo;

    @NotBlank(message = "El código interno es requerido")
    @Size(min = 3, max = 10, message = "El código interno debe tener entre 3 y 10 caracteres")
    private String codigoInterno;

    @NotBlank(message = "El RUC es requerido")
    @Size(min = 13, max = 13, message = "El RUC debe tener 13 caracteres")
    @Pattern(regexp = "^[0-9]+$", message = "El RUC debe contener solo números")
    private String ruc;

    @NotBlank(message = "La razón social es requerida")
    @Size(min = 5, max = 100, message = "La razón social debe tener entre 5 y 100 caracteres")
    private String razonSocial;

    @NotBlank(message = "El nombre comercial es requerido")
    @Size(min = 5, max = 100, message = "El nombre comercial debe tener entre 5 y 100 caracteres")
    private String nombreComercial;

    @PastOrPresent(message = "La fecha de creación no puede ser futura")
    private LocalDateTime fechaCreacion;

    private Integer codComision;

    @NotBlank(message = "El estado es requerido")
    @Pattern(regexp = "ACT|INA", message = "El estado debe ser ACT o INA")
    private String estado;

    @PastOrPresent(message = "La fecha de inactivación no puede ser futura")
    private LocalDateTime fechaInactivacion;
} 