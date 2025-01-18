package com.banquito.cards.comision.controller.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class BancoDTO {
    private Integer codigo;
    private String codigoInterno;
    private String ruc;
    private String razonSocial;
    private String nombreComercial;
    private LocalDateTime fechaCreacion;
    private Integer codComision;
    private String estado;
    private LocalDateTime fechaInactivacion;
} 