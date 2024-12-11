package com.banquito.cards.seguridad.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "SEGURIDAD_GATEWAY")
public class SeguridadGateway implements Serializable {

    @Id
    @NotNull
    @Column(name = "COD_CLAVE_GATEWAY", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer code;
    @NotNull
    @Column(name = "CLAVE", length = 128, nullable = false)
    private String clave;
    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "FECHA_CREACION", nullable = false)
    private LocalDateTime fechaCreacion;
    @NotNull
    @Temporal(TemporalType.DATE)
    @Column(name = "FECHA_ACTIVACION", nullable = false)
    private LocalDate fechaActivacion;
    @NotNull
    @Column(name = "ESTADO", length = 3, nullable = false)
    private String estado;

    public SeguridadGateway() {}

    public SeguridadGateway(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDate getFechaActivacion() {
        return fechaActivacion;
    }

    public void setFechaActivacion(LocalDate fechaActivacion) {
        this.fechaActivacion = fechaActivacion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        SeguridadGateway that = (SeguridadGateway) o;
        return Objects.equals(code, that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(code);
    }

    @Override
    public String toString() {
        return "SeguridadGateway{" +
                "code=" + code +
                ", clave='" + clave + '\'' +
                ", fechaCreacion=" + fechaCreacion +
                ", fechaActivacion=" + fechaActivacion +
                ", estado='" + estado + '\'' +
                '}';
    }
}
