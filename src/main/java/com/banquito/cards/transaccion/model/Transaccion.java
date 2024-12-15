package com.banquito.cards.transaccion.model;



import com.banquito.cards.comision.model.Banco;
import com.banquito.cards.comision.model.Comision;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.security.Timestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "TRANSACCION")
public class Transaccion implements Serializable {

    @Id
    @Column(name = "COD_TRANSACCION", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer code;
    @NotNull
    @ManyToOne
    @JoinColumn(name = "COD_BANCO", referencedColumnName = "COD_BANCO", insertable = false, updatable = false)
    private Banco banco;
    @NotNull
    @ManyToOne
    @JoinColumn(name = "COD_COMISION", referencedColumnName = "COD_COMISION", insertable = false, updatable = false)
    private Comision comision;
    @NotNull
    @Column(name = "MONTO", precision = 18, scale = 2, nullable = false)
    private BigDecimal monto;
    @NotNull
    @Column(name = "CODIGO_MONEDA", length = 3, nullable = false)
    private String codigoMoneda;
    @NotNull
    @Column(name = "MARCA", length = 4, nullable = false)
    private String marca;
    @NotNull
    @Column(name = "FECHA_EXPIRACION_TARJETA", length = 128,nullable = false)
    private String fechaExpiracionTarjeta;
    @NotNull
    @Column(name = "NOMBRE_TARJETA", length = 128, nullable = false)
    private String nombreTarjeta;
    @NotNull
    @Column(name = "NUMERO_TARJETA", length = 128, nullable = false)
    private String numeroTarjeta;
    @NotNull
    @Column(name = "DIRECCION_TARJETA", length = 128, nullable = false)
    private String direccionTarjeta;
    @NotNull
    @Column(name = "CVV", length = 128, nullable = false)
    private String cvv;
    @NotNull
    @Column(name = "ESTADO", length = 3, nullable = false)
    private String estado;
    @Column(name = "DETALLE", length = 50)
    private String detalle;
    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "FECHA_CREACION", nullable = false)
    private LocalDateTime fechaCreacion;

    public Transaccion() {}

    public Transaccion(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Banco getBanco() {
        return banco;
    }

    public void setBanco(Banco banco) {
        this.banco = banco;
    }

    public Comision getComision() {
        return comision;
    }

    public void setComision(Comision comision) {
        this.comision = comision;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public String getCodigoMoneda() {
        return codigoMoneda;
    }

    public void setCodigoMoneda(String codigoMoneda) {
        this.codigoMoneda = codigoMoneda;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getFechaExpiracionTarjeta() {
        return fechaExpiracionTarjeta;
    }

    public void setFechaExpiracionTarjeta(String fechaExpiracionTarjeta) {
        this.fechaExpiracionTarjeta = fechaExpiracionTarjeta;
    }

    public String getNombreTarjeta() {
        return nombreTarjeta;
    }

    public void setNombreTarjeta(String nombreTarjeta) {
        this.nombreTarjeta = nombreTarjeta;
    }

    public String getNumeroTarjeta() {
        return numeroTarjeta;
    }

    public void setNumeroTarjeta(String numeroTarjeta) {
        this.numeroTarjeta = numeroTarjeta;
    }

    public String getDireccionTarjeta() {
        return direccionTarjeta;
    }

    public void setDireccionTarjeta(String direccionTarjeta) {
        this.direccionTarjeta = direccionTarjeta;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
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

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Transaccion that = (Transaccion) o;
        return Objects.equals(code, that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(code);
    }

    @Override
    public String toString() {
        return "Transaccion{" +
                "code=" + code +
                ", banco=" + banco +
                ", comision=" + comision +
                ", monto=" + monto +
                ", codigoMoneda='" + codigoMoneda + '\'' +
                ", marca='" + marca + '\'' +
                ", fechaExpiracionTarjeta='" + fechaExpiracionTarjeta + '\'' +
                ", nombreTarjeta='" + nombreTarjeta + '\'' +
                ", numeroTarjeta='" + numeroTarjeta + '\'' +
                ", direccionTarjeta='" + direccionTarjeta + '\'' +
                ", cvv='" + cvv + '\'' +
                ", estado='" + estado + '\'' +
                ", detalle='" + detalle + '\'' +
                ", fechaCreacion=" + fechaCreacion +
                '}';
    }
}
