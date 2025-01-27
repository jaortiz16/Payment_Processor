package com.banquito.cards.comision.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Embeddable
public class ComisionPK implements Serializable {

    @Column(name = "COD_COMISION", nullable = false, length = 20)
    private Integer codComision;

    @Column(name = "TRANSACCIONES_DESDE", precision = 9, scale = 0, nullable = false)
    private BigDecimal codSegmento;
}
