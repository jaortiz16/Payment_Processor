package com.banquito.cards.transaccion.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.security.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "HISTORIAL_ESTADO_TRANSACCION")
public class HistorialEstadoTransaccion implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "COD_HISTORIAL_ESTADO", nullable = false)
    private Integer code;
    @NotNull
    @ManyToOne
    @JoinColumn(name = "COD_TRANSACCION", referencedColumnName = "COD_TRANSACCION")
    private Transaccion transaccion;
    @NotNull
    @Column(name = "ESTADO", length = 3, nullable = false)
    private String estado;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "FECHA_ESTADO_CAMBIO")
    private LocalDateTime fechaEstadoCambio;
    @Column(name = "DETALLE", length = 200)
    private String detalle;

    public HistorialEstadoTransaccion() {}

    public HistorialEstadoTransaccion(Integer code) {
        this.code = code;
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

    public LocalDateTime getFechaEstadoCambio() {
        return fechaEstadoCambio;
    }

    public void setFechaEstadoCambio(LocalDateTime fechaEstadoCambio) {
        this.fechaEstadoCambio = fechaEstadoCambio;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        HistorialEstadoTransaccion that = (HistorialEstadoTransaccion) o;
        return Objects.equals(code, that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(code);
    }

    @Override
    public String toString() {
        return "HistorialEstadoTransaccion{" +
                "code=" + code +
                ", transaccion=" + transaccion +
                ", estado='" + estado + '\'' +
                ", fechaEstadoCambio=" + fechaEstadoCambio +
                ", detalle='" + detalle + '\'' +
                '}';
    }
}
