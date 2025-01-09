package com.banquito.cards.fraude.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "REGLA_FRAUDE")
public class ReglaFraude implements Serializable {

    @Id
    @Column(name = "COD_REGLA", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codRegla;

    @NotNull
    @Column(name = "NOMBRE_REGLA", length = 50, nullable = false)
    private String nombreRegla;

    @NotNull
    @Column(name = "TIPO_REGLA", length = 3, nullable = false)
    private String tipoRegla; // TRX (transacciones), MNT (monto), GEO (ubicación)

    @Column(name = "LIMITE_TRANSACCIONES", precision = 9, scale = 0)
    private BigDecimal limiteTransacciones;

    @NotNull
    @Column(name = "PERIODO_TIEMPO", length = 3)
    private String periodoTiempo;

    @Column(name = "LIMITE_MONTO_TOTAL", precision = 18, scale = 2)
    private BigDecimal limiteMontoTotal;

    @NotNull
    @Column(name = "NIVEL_RIESGO", length = 3)
    private String nivelRiesgo;

    @NotNull
    @Column(name = "ESTADO", length = 3, nullable = false)
    private String estado; 

    @NotNull
    @Column(name = "PRIORIDAD", nullable = false)
    private Integer prioridad;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "FECHA_CREACION", nullable = false)
    private LocalDateTime fechaCreacion;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "FECHA_ACTUALIZACION")
    private LocalDateTime fechaActualizacion;

    public ReglaFraude() {}

    public Integer getCodRegla() {
        return codRegla;
    }

    public void setCodRegla(Integer codRegla) {
        this.codRegla = codRegla;
    }

    public String getNombreRegla() {
        return nombreRegla;
    }

    public void setNombreRegla(String nombreRegla) {
        this.nombreRegla = nombreRegla;
    }

    public String getTipoRegla() {
        return tipoRegla;
    }

    public void setTipoRegla(String tipoRegla) {
        this.tipoRegla = tipoRegla;
    }

    public BigDecimal getLimiteTransacciones() {
        return limiteTransacciones;
    }

    public void setLimiteTransacciones(BigDecimal limiteTransacciones) {
        this.limiteTransacciones = limiteTransacciones;
    }

    public String getPeriodoTiempo() {
        return periodoTiempo;
    }

    public void setPeriodoTiempo(String periodoTiempo) {
        this.periodoTiempo = periodoTiempo;
    }

    public BigDecimal getLimiteMontoTotal() {
        return limiteMontoTotal;
    }

    public void setLimiteMontoTotal(BigDecimal limiteMontoTotal) {
        this.limiteMontoTotal = limiteMontoTotal;
    }

    public String getNivelRiesgo() {
        return nivelRiesgo;
    }

    public void setNivelRiesgo(String nivelRiesgo) {
        this.nivelRiesgo = nivelRiesgo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Integer getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(Integer prioridad) {
        this.prioridad = prioridad;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
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
        ReglaFraude that = (ReglaFraude) o;
        return codRegla.equals(that.codRegla);
    }

    @Override
    public int hashCode() {
        return codRegla.hashCode();
    }

    @Override
    public String toString() {
        return "ReglaFraude{" +
                "codRegla=" + codRegla +
                ", nombreRegla='" + nombreRegla + '\'' +
                ", tipoRegla='" + tipoRegla + '\'' +
                ", limiteTransacciones=" + limiteTransacciones +
                ", periodoTiempo='" + periodoTiempo + '\'' +
                ", limiteMontoTotal=" + limiteMontoTotal +
                ", nivelRiesgo='" + nivelRiesgo + '\'' +
                ", estado='" + estado + '\'' +
                ", prioridad=" + prioridad +
                ", fechaCreacion=" + fechaCreacion +
                ", fechaActualizacion=" + fechaActualizacion +
                '}';
    }
}

