package com.banquito.cards.seguridad.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "LOG_CONEXION")
public class LogConexion implements Serializable {

    @Id
    @NotNull
    @Column(name = "COD_LOG", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer code;
    @NotNull
    @ManyToOne
    @JoinColumn(name = "MARCA", referencedColumnName = "MARCA", insertable = false, updatable = false)
    private SeguridadMarca seguridadMarca;
    @NotNull
    @ManyToOne
    @JoinColumn(name = "COD_SEGURIDAD_BANCO", referencedColumnName = "COD_SEGURIDAD_BANCO", insertable = false, updatable = false)
    private SeguridadBanco seguridadBanco;
    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "FECHA", nullable = false)
    private LocalDateTime fecha;
    @NotNull
    @Column(name = "IP_ORIGEN", length = 15, nullable = false)
    private String ipOrigen;
    @NotNull
    @Column(name = "OPERACION", length = 50, nullable = false)
    private String operacion;
    @NotNull
    @Column(name = "RESULTADO", length = 3, nullable = false)
    private String resultado;

    public LogConexion() {}

    public LogConexion(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public SeguridadMarca getSeguridadMarca() {
        return seguridadMarca;
    }

    public void setSeguridadMarca(SeguridadMarca seguridadMarca) {
        this.seguridadMarca = seguridadMarca;
    }

    public SeguridadBanco getSeguridadBanco() {
        return seguridadBanco;
    }

    public void setSeguridadBanco(SeguridadBanco seguridadBanco) {
        this.seguridadBanco = seguridadBanco;
    }

    public String getIpOrigen() {
        return ipOrigen;
    }

    public void setIpOrigen(String ipOrigen) {
        this.ipOrigen = ipOrigen;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getOperacion() {
        return operacion;
    }

    public void setOperacion(String operacion) {
        this.operacion = operacion;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        LogConexion that = (LogConexion) o;
        return Objects.equals(code, that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(code);
    }

    @Override
    public String toString() {
        return "LogConexion{" +
                "code=" + code +
                ", seguridadMarca=" + seguridadMarca +
                ", seguridadBanco=" + seguridadBanco +
                ", fecha=" + fecha +
                ", ipOrigen='" + ipOrigen + '\'' +
                ", operacion='" + operacion + '\'' +
                ", resultado='" + resultado + '\'' +
                '}';
    }
}
