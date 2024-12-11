package com.banquito.cards.seguridad.model;


import jakarta.persistence.*;

import java.io.Serializable;
import java.security.Timestamp;
import java.util.Date;

@Entity
@Table(name = "LOG_CONEXION")
public class LogConexion implements Serializable {

    @Id
    @Column(name = "COD_LOG", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codLog;

    @ManyToOne
    @JoinColumn(name = "MARCA", referencedColumnName = "MARCA", insertable = false, updatable = false)
    private SeguridadMarca seguridadMarca;

    @ManyToOne
    @JoinColumn(name = "COD_SEGURIDAD_BANCO", referencedColumnName = "COD_SEGURIDAD_BANCO", insertable = false, updatable = false)
    private SeguridadBanco seguridadBanco;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "FECHA", nullable = false)
    private Timestamp fecha;

    @Column(name = "IP_ORIGEN", length = 15, nullable = false)
    private String ipOrigen;

    @Column(name = "OPERACION", length = 50, nullable = false)
    private String operacion;

    @Column(name = "RESULTADO", length = 3, nullable = false)
    private String resultado;

    public LogConexion() {}

    public Integer getCodLog() {
        return codLog;
    }

    public void setCodLog(Integer codLog) {
        this.codLog = codLog;
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

    public Timestamp getFecha() {
        return fecha;
    }

    public void setFecha(Timestamp fecha) {
        this.fecha = fecha;
    }

    public String getIpOrigen() {
        return ipOrigen;
    }

    public void setIpOrigen(String ipOrigen) {
        this.ipOrigen = ipOrigen;
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

    public LogConexion(Integer codLog, SeguridadMarca seguridadMarca, SeguridadBanco seguridadBanco) {
        this.codLog = codLog;
        this.seguridadMarca = seguridadMarca;
        this.seguridadBanco = seguridadBanco;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        LogConexion that = (LogConexion) o;
        return codLog.equals(that.codLog) && seguridadMarca.equals(that.seguridadMarca) && seguridadBanco.equals(that.seguridadBanco);
    }

    @Override
    public int hashCode() {
        int result = codLog.hashCode();
        result = 31 * result + seguridadMarca.hashCode();
        result = 31 * result + seguridadBanco.hashCode();
        return result;
    }
}
