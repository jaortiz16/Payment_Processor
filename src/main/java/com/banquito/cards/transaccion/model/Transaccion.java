package com.banquito.cards.transaccion.model;

import com.banquito.cards.comision.model.Banco;
import com.banquito.cards.comision.model.Comision;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "TRANSACCION")
public class Transaccion implements Serializable {

    @Id
    @Column(name = "COD_TRANSACCION", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codigo;
    
    @NotNull
    @ManyToOne
    @JoinColumn(name = "COD_BANCO", referencedColumnName = "COD_BANCO", nullable = false)
    private Banco banco;
    
    @NotNull
    @ManyToOne
    @JoinColumn(name = "COD_COMISION", referencedColumnName = "COD_COMISION")
    private Comision comision;
    
    @NotNull
    @Column(name = "MONTO", nullable = false, precision = 18, scale = 2)
    private BigDecimal monto = BigDecimal.ZERO;

    @NotNull
    @Column(name = "MODALIDAD", length = 3, nullable = false)
    private String modalidad; // SIM (simple), REC (recurrente)
    
    @NotNull
    @Column(name = "CODIGO_MONEDA", length = 3, nullable = false)
    private String codigoMoneda;
    
    @NotNull
    @Column(name = "MARCA", length = 4, nullable = false)
    private String marca;
    
    @NotNull
    @Column(name = "FECHA_EXPIRACION_TARJETA", length = 64, nullable = false)
    private String fechaExpiracionTarjeta;
    
    @NotNull
    @Column(name = "NOMBRE_TARJETA", length = 128, nullable = false)
    private String nombreTarjeta;
    
    @NotNull
    @Column(name = "NUMERO_TARJETA", length = 128, nullable = false)
    private String numeroTarjeta;
    
    @NotNull
    @Column(name = "DIRECCION_TARJETA", length = 256, nullable = false)
    private String direccionTarjeta;
    
    @NotNull
    @Column(name = "CVV", length = 128, nullable = false)
    private String cvv;

    @NotNull
    @Column(name = "PAIS", length = 2, nullable = false)
    private String pais;
    
    @NotNull
    @Column(name = "ESTADO", length = 3, nullable = false)
    private String estado;
    
    @Column(name = "DETALLE", length = 50)
    private String detalle;

    @Column(name = "CODIGO_UNICO_TRANSACCION", length = 64)
    private String codigoUnicoTransaccion;
    
    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "FECHA_CREACION", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "FECHA_EJECUCION_RECURRENCIA")
    private LocalDateTime fechaEjecucionRecurrencia;

    @Column(name = "FECHA_FIN_RECURRENCIA")
    private LocalDateTime fechaFinRecurrencia;

    @Column(name = "GTW_COMISION", length = 20)
    private String gtwComision = "0.00";

    @Column(name = "GTW_CUENTA", length = 20)
    private String gtwCuenta;

    @Column(name = "NUMERO_CUENTA", length = 20)
    private String numeroCuenta;

    @Column(name = "CUOTAS")
    private Integer cuotas;

    @Column(name = "INTERES_DIFERIDO")
    private Boolean interesDiferido;

    @Column(name = "BENEFICIARIO", length = 128)
    private String beneficiario;

    public Transaccion() {}

    public Transaccion(Integer codigo) {
        this.codigo = codigo;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
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

    public String getModalidad() {
        return modalidad;
    }

    public void setModalidad(String modalidad) {
        this.modalidad = modalidad;
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

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
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

    public String getCodigoUnicoTransaccion() {
        return codigoUnicoTransaccion;
    }

    public void setCodigoUnicoTransaccion(String codigoUnicoTransaccion) {
        this.codigoUnicoTransaccion = codigoUnicoTransaccion;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaEjecucionRecurrencia() {
        return fechaEjecucionRecurrencia;
    }

    public void setFechaEjecucionRecurrencia(LocalDateTime fechaEjecucionRecurrencia) {
        this.fechaEjecucionRecurrencia = fechaEjecucionRecurrencia;
    }

    public LocalDateTime getFechaFinRecurrencia() {
        return fechaFinRecurrencia;
    }

    public void setFechaFinRecurrencia(LocalDateTime fechaFinRecurrencia) {
        this.fechaFinRecurrencia = fechaFinRecurrencia;
    }

    public String getGtwComision() {
        return gtwComision;
    }

    public void setGtwComision(String gtwComision) {
        this.gtwComision = gtwComision;
    }

    public String getGtwCuenta() {
        return gtwCuenta;
    }

    public void setGtwCuenta(String gtwCuenta) {
        this.gtwCuenta = gtwCuenta;
    }

    public String getNumeroCuenta() {
        return numeroCuenta;
    }

    public void setNumeroCuenta(String numeroCuenta) {
        this.numeroCuenta = numeroCuenta;
    }

    public Integer getCuotas() {
        return cuotas;
    }

    public void setCuotas(Integer cuotas) {
        this.cuotas = cuotas;
    }

    public Boolean getInteresDiferido() {
        return interesDiferido;
    }

    public void setInteresDiferido(Boolean interesDiferido) {
        this.interesDiferido = interesDiferido;
    }

    public String getBeneficiario() {
        return beneficiario;
    }

    public void setBeneficiario(String beneficiario) {
        this.beneficiario = beneficiario;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Transaccion that = (Transaccion) o;
        return Objects.equals(codigo, that.codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(codigo);
    }

    @Override
    public String toString() {
        return "Transaccion{" +
                "codigo=" + codigo +
                ", banco=" + banco +
                ", comision=" + comision +
                ", monto=" + monto +
                ", modalidad='" + modalidad + '\'' +
                ", codigoMoneda='" + codigoMoneda + '\'' +
                ", marca='" + marca + '\'' +
                ", fechaExpiracionTarjeta='" + fechaExpiracionTarjeta + '\'' +
                ", nombreTarjeta='" + nombreTarjeta + '\'' +
                ", numeroTarjeta='" + numeroTarjeta + '\'' +
                ", direccionTarjeta='" + direccionTarjeta + '\'' +
                ", cvv='" + cvv + '\'' +
                ", pais='" + pais + '\'' +
                ", estado='" + estado + '\'' +
                ", detalle='" + detalle + '\'' +
                ", codigoUnicoTransaccion='" + codigoUnicoTransaccion + '\'' +
                ", fechaCreacion=" + fechaCreacion +
                ", fechaEjecucionRecurrencia=" + fechaEjecucionRecurrencia +
                ", fechaFinRecurrencia=" + fechaFinRecurrencia +
                '}';
    }
}
