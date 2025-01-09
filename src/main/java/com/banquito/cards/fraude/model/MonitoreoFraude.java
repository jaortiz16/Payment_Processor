package com.banquito.cards.fraude.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import com.banquito.cards.transaccion.model.Transaccion;

@Entity
@Table(name = "MONITOREO_FRAUDE")
public class MonitoreoFraude implements Serializable {

    @Id
    @Column(name = "COD_MONITOREO_FRAUDE", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codigo;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "COD_REGLA", referencedColumnName = "COD_REGLA")
    private ReglaFraude reglaFraude;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "COD_TRANSACCION", referencedColumnName = "COD_TRANSACCION")
    private Transaccion transaccion;

    @NotNull
    @Column(name = "NIVEL_RIESGO", length = 3, nullable = false)
    private String nivelRiesgo; // BAJ, MED, ALT

    @NotNull
    @Column(name = "PUNTAJE_RIESGO", precision = 5, scale = 2, nullable = false)
    private BigDecimal puntajeRiesgo; // 0.00 - 100.00

    @NotNull
    @Column(name = "ESTADO", length = 3, nullable = false)
    private String estado; // PEN (pendiente), PRO (procesado)

    @Column(name = "DETALLE", length = 200)
    private String detalle;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "FECHA_DETECCION", nullable = false)
    private LocalDateTime fechaDeteccion;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "FECHA_PROCESAMIENTO")
    private LocalDateTime fechaProcesamiento;

    @Column(name = "CODIGO_UNICO_TRANSACCION", length = 64)
    private String codigoUnicoTransaccion;

    public MonitoreoFraude() {}

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public ReglaFraude getReglaFraude() {
        return reglaFraude;
    }

    public void setReglaFraude(ReglaFraude reglaFraude) {
        this.reglaFraude = reglaFraude;
    }

    public Transaccion getTransaccion() {
        return transaccion;
    }

    public void setTransaccion(Transaccion transaccion) {
        this.transaccion = transaccion;
    }

    public String getNivelRiesgo() {
        return nivelRiesgo;
    }

    public void setNivelRiesgo(String nivelRiesgo) {
        this.nivelRiesgo = nivelRiesgo;
    }

    public BigDecimal getPuntajeRiesgo() {
        return puntajeRiesgo;
    }

    public void setPuntajeRiesgo(BigDecimal puntajeRiesgo) {
        this.puntajeRiesgo = puntajeRiesgo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public LocalDateTime getFechaDeteccion() {
        return fechaDeteccion;
    }

    public void setFechaDeteccion(LocalDateTime fechaDeteccion) {
        this.fechaDeteccion = fechaDeteccion;
    }

    public LocalDateTime getFechaProcesamiento() {
        return fechaProcesamiento;
    }

    public void setFechaProcesamiento(LocalDateTime fechaProcesamiento) {
        this.fechaProcesamiento = fechaProcesamiento;
    }

    public String getCodigoUnicoTransaccion() {
        return codigoUnicoTransaccion;
    }

    public void setCodigoUnicoTransaccion(String codigoUnicoTransaccion) {
        this.codigoUnicoTransaccion = codigoUnicoTransaccion;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        MonitoreoFraude that = (MonitoreoFraude) o;
        return codigo.equals(that.codigo);
    }

    @Override
    public int hashCode() {
        return codigo.hashCode();
    }

    @Override
    public String toString() {
        return "MonitoreoFraude{" +
                "codigo=" + codigo +
                ", reglaFraude=" + reglaFraude +
                ", transaccion=" + transaccion +
                ", nivelRiesgo='" + nivelRiesgo + '\'' +
                ", puntajeRiesgo=" + puntajeRiesgo +
                ", estado='" + estado + '\'' +
                ", detalle='" + detalle + '\'' +
                ", fechaDeteccion=" + fechaDeteccion +
                ", fechaProcesamiento=" + fechaProcesamiento +
                '}';
    }
}
