package com.banquito.cards.comision;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;
@Embeddable
public class ComisionPK implements Serializable {


    @Column(name = "COD_COMISION",nullable = false)
    private Integer codComision;

    @Column(name = "TRANSACCIONES_DESDE", precision = 9, scale = 0, nullable = false)
    private BigDecimal codSegmento;

    public ComisionPK() {}

    public ComisionPK(Integer codComision, BigDecimal codSegmento) {
        this.codComision = codComision;
        this.codSegmento = codSegmento;
    }

    public Integer getCodComision() {
        return codComision;
    }

    public void setCodComision(Integer codComision) {
        this.codComision = codComision;
    }

    public BigDecimal getCodSegmento() {
        return codSegmento;
    }

    public void setCodSegmento(BigDecimal codSegmento) {
        this.codSegmento = codSegmento;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((codComision == null) ? 0 : codComision.hashCode());
        result = prime * result + ((codSegmento == null) ? 0 : codSegmento.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ComisionPK other = (ComisionPK) obj;
        if (codComision == null) {
            if (other.codComision != null)
                return false;
        } else if (!codComision.equals(other.codComision))
            return false;
        if (codSegmento == null) {
            if (other.codSegmento != null)
                return false;
        } else if (!codSegmento.equals(other.codSegmento))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "ComisionPK [codComision=" + codComision + ", codSegmento=" + codSegmento + "]";
    }

}
