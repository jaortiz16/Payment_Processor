package com.banquito.cards.comision;


import jakarta.persistence.*;

import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "COMISION_SEGMENTO")
public class ComisionSegmento implements Serializable {

    @EmbeddedId
    private ComisionPK id;

    @Column(name = "TRANSACCIONES_HASTA", precision = 9, scale = 0, nullable = false)
    private BigDecimal transaccionesHasta;

    @Column(name = "MONTO", precision = 20, scale = 4, nullable = false)
    private BigDecimal monto;

    @ManyToOne
    @JoinColumn(name = "COD_COMISION", referencedColumnName = "COD_COMISION", insertable = false, updatable = false)
    private Comision Comision;

    public ComisionSegmento(){}

    public ComisionPK getId() {
        return id;
    }

    public void setId(ComisionPK id) {
        this.id = id;
    }

    public BigDecimal getTransaccionesHasta() {
        return transaccionesHasta;
    }

    public void setTransaccionesHasta(BigDecimal transaccionesHasta) {
        this.transaccionesHasta = transaccionesHasta;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public Comision getComision() {
        return Comision;
    }

    public void setComision(Comision comision) {
        Comision = comision;
    }

    public ComisionSegmento(ComisionPK id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        ComisionSegmento that = (ComisionSegmento) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
