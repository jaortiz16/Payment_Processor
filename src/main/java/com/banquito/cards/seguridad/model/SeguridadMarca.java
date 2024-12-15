package com.banquito.cards.seguridad.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "SEGURIDAD_MARCA")
public class SeguridadMarca implements Serializable {

    @Id
    @Column(name = "MARCA", length = 4, nullable = false)
    private String marca;
    @NotNull
    @Column(name = "CLAVE", length = 128, nullable = false)
    private String clave;
    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "FECHA_ACTUALIZACION", nullable = false)
    private LocalDateTime fechaActualizacion;

    public SeguridadMarca() {}

    public SeguridadMarca(String marca) {
        this.marca = marca;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        SeguridadMarca that = (SeguridadMarca) o;
        return Objects.equals(marca, that.marca);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(marca);
    }

    @Override
    public String toString() {
        return "SeguridadMarca{" +
                "marca='" + marca + '\'' +
                ", clave='" + clave + '\'' +
                ", fechaActualizacion=" + fechaActualizacion +
                '}';
    }
}
