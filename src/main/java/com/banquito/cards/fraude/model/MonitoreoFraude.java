package com.banquito.cards.fraude.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.security.Timestamp;
import java.time.LocalDateTime;


@Entity
@Table(name = "MONITOREO_FRAUDE")
public class MonitoreoFraude implements Serializable {

    @Id
    @Column(name = "COD_MONITOREO_FRAUDE", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer code;
    @ManyToOne
    @JoinColumn(name = "COD_REGLA", referencedColumnName = "COD_REGLA")
    private ReglaFraude reglaFraude;
    @NotNull
    @Column(name = "RIESGO", length = 5, nullable = false)
    private String riesgo;
    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "FECHA_DETECCION", nullable = false)
    private LocalDateTime fechaDeteccion;

    public MonitoreoFraude() {}

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public ReglaFraude getReglaFraude() {
        return reglaFraude;
    }

    public void setReglaFraude(ReglaFraude reglaFraude) {
        this.reglaFraude = reglaFraude;
    }

    public String getRiesgo() {
        return riesgo;
    }

    public void setRiesgo(String riesgo) {
        this.riesgo = riesgo;
    }

    public LocalDateTime getFechaDeteccion() {
        return fechaDeteccion;
    }

    public void setFechaDeteccion(LocalDateTime fechaDeteccion) {
        this.fechaDeteccion = fechaDeteccion;
    }

    public MonitoreoFraude(Integer code, ReglaFraude reglaFraude) {
        this.code = code;
        this.reglaFraude = reglaFraude;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        MonitoreoFraude that = (MonitoreoFraude) o;
        return code.equals(that.code) && reglaFraude.equals(that.reglaFraude);
    }

    @Override
    public int hashCode() {
        int result = code.hashCode();
        result = 31 * result + reglaFraude.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "MonitoreoFraude{" +
                "code=" + code +
                ", reglaFraude=" + reglaFraude +
                ", riesgo='" + riesgo + '\'' +
                ", fechaDeteccion=" + fechaDeteccion +
                '}';
    }
}
