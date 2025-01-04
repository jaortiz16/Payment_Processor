package com.banquito.cards.comision.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

@Embeddable
public class ComisionPK implements Serializable {

    @Column(name = "COD_COMISION",nullable = false, length = 20)
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
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ComisionPK that = (ComisionPK) o;
        return Objects.equals(codComision, that.codComision) && Objects.equals(codSegmento, that.codSegmento);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codComision, codSegmento);
    }

    @Override
    public String toString() {
        return "ComisionPK{" +
                "codComision=" + codComision +
                ", codSegmento=" + codSegmento +
                '}';
    }
}
