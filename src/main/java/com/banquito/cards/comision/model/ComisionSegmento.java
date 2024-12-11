package com.banquito.cards.comision.model;



import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

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

    public ComisionSegmento() {
    }

    public ComisionSegmento(ComisionPK pk) {
        this.pk = pk;
    }

    public ComisionPK getPk() {
        return pk;
    }

    public void setPk(ComisionPK pk) {
        this.pk = pk;
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
        return comision;
    }

    public void setComision(Comision comision) {
        this.comision = comision;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ComisionSegmento that = (ComisionSegmento) o;
        return Objects.equals(pk, that.pk);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(pk);
    }

    @Override
    public String toString() {
        return "ComisionSegmento{" +
                "pk=" + pk +
                ", transaccionesHasta=" + transaccionesHasta +
                ", monto=" + monto +
                ", comision=" + comision +
                '}';
    }
}
