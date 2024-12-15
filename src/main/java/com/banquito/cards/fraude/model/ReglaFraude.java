package com.banquito.cards.fraude.model;

import com.banquito.cards.transaccion.model.Transaccion;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.security.Timestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "REGLA_FRAUDE")
public class ReglaFraude implements Serializable {

    @Id
    @Column(name = "COD_REGLA", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codRegla;
    @NotNull
    @ManyToOne
    @JoinColumn(name = "COD_TRANSACCION", referencedColumnName = "COD_TRANSACCION", insertable = false, updatable = false)
    private Transaccion transaccion;
    @NotNull
    @Column(name = "NOMBRE_REGLA", length = 50, nullable = false)
    private String nombreRegla;
    @NotNull
    @Column(name = "LIMITE_TRANSACCIONES", precision = 9, scale = 0, nullable = false)
    private BigDecimal limiteTransacciones;
    @NotNull
    @Column(name = "PERIODO_TIEMPO", length = 3, nullable = false)
    private String periodoTiempo;
    @NotNull
    @Column(name = "LIMITE_MONTO_TOTAL", precision = 18, scale = 2, nullable = false)
    private BigDecimal limiteMontoTotal;
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

    public Transaccion getTransaccion() {
        return transaccion;
    }

    public void setTransaccion(Transaccion transaccion) {
        this.transaccion = transaccion;
    }

    public String getNombreRegla() {
        return nombreRegla;
    }

    public void setNombreRegla(String nombreRegla) {
        this.nombreRegla = nombreRegla;
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

    public ReglaFraude(Integer codRegla, Transaccion transaccion) {
        this.codRegla = codRegla;
        this.transaccion = transaccion;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        ReglaFraude that = (ReglaFraude) o;
        return codRegla.equals(that.codRegla) && transaccion.equals(that.transaccion);
    }

    @Override
    public int hashCode() {
        int result = codRegla.hashCode();
        result = 31 * result + transaccion.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ReglaFraude{" +
                "codRegla=" + codRegla +
                ", transaccion=" + transaccion +
                ", nombreRegla='" + nombreRegla + '\'' +
                ", limiteTransacciones=" + limiteTransacciones +
                ", periodoTiempo='" + periodoTiempo + '\'' +
                ", limiteMontoTotal=" + limiteMontoTotal +
                ", fechaCreacion=" + fechaCreacion +
                ", fechaActualizacion=" + fechaActualizacion +
                '}';
    }
}

