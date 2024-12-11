package com.banquito.cards.comision.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.List;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "COMISION")
public class Comision implements Serializable {

    @Id
    @NotNull
    @Column(name = "COD_COMISION", nullable = false, length = 20)
    private String codComision;
    @NotNull
    @Column(name = "TIPO", length = 3, nullable = false)
    private String tipo;
    @NotNull
    @Column(name = "MONTO_BASE", precision = 20, scale = 4, nullable = false)
    private BigDecimal montoBase;
    @NotNull
    @Column(name = "TRANSACCIONES_BASE", precision = 9, scale = 0, nullable = false)
    private Integer transaccionesBase;
    @NotNull
    @Column(name = "MANEJA_SEGMENTOS", nullable = false)
    private Boolean manejaSegmentos;

    public Comision() {
    }

    public Comision(String codComision) {
        this.codComision = codComision;
    }

    public String getCodComision() {
        return codComision;
    }

    public void setCodComision(String codComision) {
        this.codComision = codComision;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public BigDecimal getMontoBase() {
        return montoBase;
    }

    public void setMontoBase(BigDecimal montoBase) {
        this.montoBase = montoBase;
    }

    public Integer getTransaccionesBase() {
        return transaccionesBase;
    }

    public void setTransaccionesBase(Integer transaccionesBase) {
        this.transaccionesBase = transaccionesBase;
    }

    public Boolean getManejaSegmentos() {
        return manejaSegmentos;
    }

    public void setManejaSegmentos(Boolean manejaSegmentos) {
        this.manejaSegmentos = manejaSegmentos;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Comision comision = (Comision) o;
        return Objects.equals(codComision, comision.codComision);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(codComision);
    }
}
