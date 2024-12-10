package com.banquito.cards.seguridad;

import jakarta.persistence.*;

import java.io.Serializable;
import java.security.Timestamp;
import java.util.Date;

@Entity
@Table(name = "SEGURIDAD_MARCA")
public class SeguridadMarca implements Serializable {

    @Id
    @Column(name = "MARCA", length = 4, nullable = false)
    private String marca;

    @Column(name = "CLAVE", length = 128, nullable = false)
    private String clave;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "FECHA_ACTUALIZACION", nullable = false)
    private Timestamp fechaActualizacion;

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

    public Timestamp getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(Timestamp fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        SeguridadMarca that = (SeguridadMarca) o;
        return marca.equals(that.marca);
    }

    @Override
    public int hashCode() {
        return marca.hashCode();
    }
}
