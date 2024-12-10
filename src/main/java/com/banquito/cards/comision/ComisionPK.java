package com.banquito.cards.comision;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

public class ComisionPK implements Serializable {


    @Column(name = "COD_COMISION",nullable = false, length = 20)
    private String codComision;

    @Column(name = "TRANSACCIONES_DESDE", precision = 9, scale = 0, nullable = false)
    private BigDecimal codSegmento;

    public ComisionPK() {}

    public ComisionPK(String codComision, BigDecimal codSegmento) {
        this.codComision = codComision;
        this.codSegmento = codSegmento;
    }

    public String getCodComision() {
        return codComision;
    }

    public void setCodComision(String codComision) {
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
}
