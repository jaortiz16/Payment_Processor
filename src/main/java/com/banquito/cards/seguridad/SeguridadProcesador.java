package com.banquito.cards.seguridad;



import jakarta.persistence.*;

import java.io.Serializable;
import java.security.Timestamp;
import java.util.Date;

@Entity
@Table(name = "SEGURIDAD_PROCESADOR")
public class SeguridadProcesador implements Serializable {

    @Id
    @Column(name = "COD_SEGURIDAD_PROCESADOR", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codSeguridadProcesador;

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

    public SeguridadProcesador() {}

    public SeguridadProcesador(Integer codSeguridadProcesador) {
        this.codSeguridadProcesador = codSeguridadProcesador;
    }

    public Integer getCodSeguridadProcesador() {
        return codSeguridadProcesador;
    }

    public void setCodSeguridadProcesador(Integer codSeguridadProcesador) {
        this.codSeguridadProcesador = codSeguridadProcesador;
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

        SeguridadProcesador that = (SeguridadProcesador) o;
        return codSeguridadProcesador.equals(that.codSeguridadProcesador);
    }

    @Override
    public int hashCode() {
        return codSeguridadProcesador.hashCode();
    }
}
