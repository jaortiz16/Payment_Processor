package com.banquito.cards.transaccion.model;



import com.banquito.cards.comision.Banco;
import com.banquito.cards.comision.Comision;
import jakarta.persistence.*;

import java.io.Serializable;
import java.security.Timestamp;
import java.util.Date;
import java.util.List;
import java.math.BigDecimal;

@Entity
@Table(name = "TRANSACCION")
public class Transaccion implements Serializable {

    @Id
    @Column(name = "COD_TRANSACCION", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codTransaccion;

    @ManyToOne
    @JoinColumn(name = "COD_BANCO", referencedColumnName = "COD_BANCO", insertable = false, updatable = false)
    private Banco banco;

    @ManyToOne
    @JoinColumn(name = "COD_COMISION", referencedColumnName = "COD_COMISION", insertable = false, updatable = false)
    private Comision comision;

    @Column(name = "MONTO", precision = 18, scale = 2, nullable = false)
    private BigDecimal monto;

    @Column(name = "CODIGO_MONEDA", length = 3, nullable = false)
    private String codigoMoneda;

    @Column(name = "MARCA", length = 4, nullable = false)
    private String marca;

    @Column(name = "FECHA_EXPIRACION_TARJETA", length = 128,nullable = false)
    private String fechaExpiracionTarjeta;

    @Column(name = "NOMBRE_TARJETA", length = 128, nullable = false)
    private String nombreTarjeta;

    @Column(name = "NUMERO_TARJETA", length = 128, nullable = false)
    private String numeroTarjeta;

    @Column(name = "DIRECCION_TARJETA", length = 128, nullable = false)
    private String direccionTarjeta;

    @Column(name = "CVV", length = 128, nullable = false)
    private String cvv;

    @Column(name = "ESTADO", length = 3, nullable = false)
    private String estado;

    @Column(name = "DETALLE", length = 50)
    private String detalle;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "FECHA_CREACION", nullable = false)
    private Timestamp fechaCreacion;

    public Transaccion() {}

    public Transaccion(Integer codTransaccion, Banco banco, Comision comision) {
        this.codTransaccion = codTransaccion;
        this.banco = banco;
        this.comision = comision;
    }

    public Integer getCodTransaccion() {
        return codTransaccion;
    }

    public void setCodTransaccion(Integer codTransaccion) {
        this.codTransaccion = codTransaccion;
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

    public Timestamp getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Timestamp fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        Transaccion that = (Transaccion) o;
        return codTransaccion.equals(that.codTransaccion) && banco.equals(that.banco) && comision.equals(that.comision);
    }

    @Override
    public int hashCode() {
        int result = codTransaccion.hashCode();
        result = 31 * result + banco.hashCode();
        result = 31 * result + comision.hashCode();
        return result;
    }
}
