package com.banquito.cards.comision.model;



import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.security.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "BANCO")
public class Banco implements Serializable {

    @Id
    @NotNull
    @Column(name = "COD_BANCO", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codBanco;
    @NotNull
    @Column(name = "CODIGO_INTERNO", length = 10, nullable = false)
    private String codigoInterno;
    @NotNull
    @Column(name = "RUC", length = 13, nullable = false)
    private String ruc;
    @NotNull
    @Column(name = "RAZON_SOCIAL", length = 100, nullable = false)
    private String razonSocial;
    @NotNull
    @Column(name = "NOMBRE_COMERCIAL", length = 100, nullable = false)
    private String nombreComercial;
    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "FECHA_CREACION", nullable = false)
    private Timestamp fechaCreacion;
    @ManyToOne
    @JoinColumn(name = "COD_COMISION", referencedColumnName = "COD_COMISION", insertable = false, updatable = false)
    private Comision comision;
    @NotNull
    @Column(name = "ESTADO", length = 3, nullable = false)
    private String estado;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "FECHA_INACTIVACION")
    private Timestamp fechaInactivacion;

    public Banco() {}

    public Banco(Integer codBanco) {
        this.codBanco = codBanco;
    }

    public Integer getCodBanco() {
        return codBanco;
    }

    public void setCodBanco(Integer codBanco) {
        this.codBanco = codBanco;
    }

    public String getCodigoInterno() {
        return codigoInterno;
    }

    public void setCodigoInterno(String codigoInterno) {
        this.codigoInterno = codigoInterno;
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getNombreComercial() {
        return nombreComercial;
    }

    public void setNombreComercial(String nombreComercial) {
        this.nombreComercial = nombreComercial;
    }

    public Timestamp getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Timestamp fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Comision getComision() {
        return comision;
    }

    public void setComision(Comision comision) {
        this.comision = comision;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Timestamp getFechaInactivacion() {
        return fechaInactivacion;
    }

    public void setFechaInactivacion(Timestamp fechaInactivacion) {
        this.fechaInactivacion = fechaInactivacion;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Banco banco = (Banco) o;
        return Objects.equals(codBanco, banco.codBanco);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(codBanco);
    }

    @Override
    public String toString() {
        return "Banco{" +
                "codBanco=" + codBanco +
                ", codigoInterno='" + codigoInterno + '\'' +
                ", ruc='" + ruc + '\'' +
                ", razonSocial='" + razonSocial + '\'' +
                ", nombreComercial='" + nombreComercial + '\'' +
                ", fechaCreacion=" + fechaCreacion +
                ", comision=" + comision +
                ", estado='" + estado + '\'' +
                ", fechaInactivacion=" + fechaInactivacion +
                '}';
    }
}
