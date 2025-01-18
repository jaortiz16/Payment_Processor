package com.banquito.cards.comision.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@Entity
@Table(name = "COMISION_SEGMENTO")
public class ComisionSegmento implements Serializable {

    @EmbeddedId
    private ComisionPK pk;

    @NotNull
    @Column(name = "TRANSACCIONES_HASTA", precision = 9, scale = 0, nullable = false)
    private BigDecimal transaccionesHasta;

    @NotNull
    @Column(name = "MONTO", precision = 20, scale = 4, nullable = false)
    private BigDecimal monto;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "COD_COMISION", referencedColumnName = "COD_COMISION", insertable = false, updatable = false)
    private Comision comision;

    public ComisionSegmento(ComisionPK pk) {
        this.pk = pk;
    }
}
