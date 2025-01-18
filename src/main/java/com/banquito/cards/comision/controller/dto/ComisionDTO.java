package com.banquito.cards.comision.controller.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class ComisionDTO {
    private Integer codigo;
    private String tipo;
    private BigDecimal montoBase;
    private Integer transaccionesBase;
    private Boolean manejaSegmentos;
} 