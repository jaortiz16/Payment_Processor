package com.banquito.cards.comision.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "COMISION")
public class Comision implements Serializable {

    public static final String TIPO_PORCENTAJE = "POR";
    public static final String TIPO_FIJO = "FIJ";

    @Id
    @Column(name = "COD_COMISION", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codigo;

    @NotNull
    @Column(name = "TIPO", length = 3, nullable = false)
    private String tipo;

    @NotNull
    @PositiveOrZero
    @Column(name = "MONTO_BASE", precision = 20, scale = 4, nullable = false)
    private BigDecimal montoBase;

    @NotNull
    @PositiveOrZero
    @Column(name = "TRANSACCIONES_BASE", precision = 9, scale = 0, nullable = false)
    private Integer transaccionesBase;

    @NotNull
    @Column(name = "MANEJA_SEGMENTOS", nullable = false)
    private Boolean manejaSegmentos;

    public Comision() {
    }

    public Comision(Integer codigo) {
        this.codigo = codigo;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
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
        return Objects.equals(codigo, comision.codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(codigo);
    }

    @Override
    public String toString() {
        return "Comision{" +
                "codigo=" + codigo +
                ", tipo='" + tipo + '\'' +
                ", montoBase=" + montoBase +
                ", transaccionesBase=" + transaccionesBase +
                ", manejaSegmentos=" + manejaSegmentos +
                '}';
    }
}
