package com.banquito.cards.seguridad;

import jakarta.persistence.*;

import java.io.Serializable;
import java.security.Timestamp;
import java.util.Date;

@Entity
@Table(name = "SEGURIDAD_BANCO")
public class SeguridadBanco implements Serializable {

    @Id
    @Column(name = "COD_SEGURIDAD_BANCO", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codSeguridadBanco;

    @Column(name = "CLAVE", length = 128, nullable = false)
    private String clave;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "FECHA_ACTUALIZACION", nullable = false)
    private Timestamp fechaActualizacion;

    @Temporal(TemporalType.DATE)
    @Column(name = "FECHA_ACTIVACION", nullable = false)
    private Date fechaActivacion;

    @Column(name = "ESTADO", length = 3, nullable = false)
    private String estado;

    public SeguridadBanco() {}

    public SeguridadBanco(Integer codSeguridadBanco) {
        this.codSeguridadBanco = codSeguridadBanco;
    }

    public Integer getCodSeguridadBanco() {
        return codSeguridadBanco;
    }

    public void setCodSeguridadBanco(Integer codSeguridadBanco) {
        this.codSeguridadBanco = codSeguridadBanco;
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

    public Date getFechaActivacion() {
        return fechaActivacion;
    }

    public void setFechaActivacion(Date fechaActivacion) {
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

        SeguridadBanco that = (SeguridadBanco) o;
        return codSeguridadBanco.equals(that.codSeguridadBanco);
    }

    @Override
    public int hashCode() {
        return codSeguridadBanco.hashCode();
    }
}
