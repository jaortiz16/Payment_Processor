package com.banquito.cards.comision.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class ComisionPK implements Serializable {

    @Column(name = "COD_COMISION", nullable = false, length = 20)
    private Integer codComision;

    @Column(name = "TRANSACCIONES_DESDE", precision = 9, scale = 0, nullable = false)
    private BigDecimal codSegmento;
}
