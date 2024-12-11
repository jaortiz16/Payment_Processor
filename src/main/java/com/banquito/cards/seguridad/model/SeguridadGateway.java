package com.banquito.cards.seguridad.model;

import jakarta.persistence.*;

import java.io.Serializable;
import java.security.Timestamp;
import java.util.Date;

@Entity
@Table(name = "SEGURIDAD_GATEWAY")
public class SeguridadGateway implements Serializable {

    @Id
    @Column(name = "COD_CLAVE_GATEWAY", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codClaveGateway;

    @Column(name = "CLAVE", length = 128, nullable = false)
    private String clave;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "FECHA_CREACION", nullable = false)
    private Timestamp fechaCreacion;

    @Temporal(TemporalType.DATE)
    @Column(name = "FECHA_ACTIVACION", nullable = false)
    private Date fechaActivacion;

    @Column(name = "ESTADO", length = 3, nullable = false)
    private String estado;

    public SeguridadGateway() {}

    public SeguridadGateway(Integer codClaveGateway) {
        this.codClaveGateway = codClaveGateway;
    }

    public Integer getCodClaveGateway() {
        return codClaveGateway;
    }

    public void setCodClaveGateway(Integer codClaveGateway) {
        this.codClaveGateway = codClaveGateway;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public Timestamp getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Timestamp fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
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

        SeguridadGateway that = (SeguridadGateway) o;
        return codClaveGateway.equals(that.codClaveGateway);
    }

    @Override
    public int hashCode() {
        return codClaveGateway.hashCode();
    }
}
