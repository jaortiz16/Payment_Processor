package com.banquito.cards.transaccion.model;

import jakarta.persistence.*;

import java.io.Serializable;
import java.security.Timestamp;
import java.util.Date;

@Entity
@Table(name = "HISTORIAL_ESTADO_TRANSACCION")
public class HistorialEstadoTransaccion implements Serializable {

    @Id
    @Column(name = "COD_HISTORIAL_ESTADO", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer code;

    @ManyToOne
    @JoinColumn(name = "COD_TRANSACCION", referencedColumnName = "COD_TRANSACCION", insertable = false, updatable = false)
    private Transaccion transaccion;

    @Column(name = "ESTADO", length = 3, nullable = false)
    private String estado;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "FECHA_ESTADO_CAMBIO")
    private Timestamp fechaEstadoCambio;

    @Column(name = "DETALLE", length = 50)
    private String detalle;

    public HistorialEstadoTransaccion() {
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Transaccion getTransaccion() {
        return transaccion;
    }

    public void setTransaccion(Transaccion transaccion) {
        this.transaccion = transaccion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Timestamp getFechaEstadoCambio() {
        return fechaEstadoCambio;
    }

    public void setFechaEstadoCambio(Timestamp fechaEstadoCambio) {
        this.fechaEstadoCambio = fechaEstadoCambio;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public HistorialEstadoTransaccion(Integer code) {
        this.code = code;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        HistorialEstadoTransaccion that = (HistorialEstadoTransaccion) o;
        return code.equals(that.code);
    }

    @Override
    public int hashCode() {
        return code.hashCode();
    }
}
