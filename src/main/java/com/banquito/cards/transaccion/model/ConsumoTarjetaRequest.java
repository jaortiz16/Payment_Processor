package com.banquito.cards.transaccion.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ConsumoTarjetaRequest {
    private String numeroTarjeta;
    private String cvv;
    private String fechaCaducidad;
    private BigDecimal valor;
    private String descripcion;
    private String numeroCuenta;
    private Boolean esDiferido;
    private Integer cuotas;
    private Boolean interesDiferido;
    private String beneficiario;

    public ConsumoTarjetaRequest() {}

    public String getNumeroTarjeta() {
        return numeroTarjeta;
    }

    public void setNumeroTarjeta(String numeroTarjeta) {
        this.numeroTarjeta = numeroTarjeta;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public String getFechaCaducidad() {
        return fechaCaducidad;
    }

    public void setFechaCaducidad(String fechaCaducidad) {
        this.fechaCaducidad = fechaCaducidad;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getNumeroCuenta() {
        return numeroCuenta;
    }

    public void setNumeroCuenta(String numeroCuenta) {
        this.numeroCuenta = numeroCuenta;
    }

    public Boolean getEsDiferido() {
        return esDiferido;
    }

    public void setEsDiferido(Boolean esDiferido) {
        this.esDiferido = esDiferido;
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
} 