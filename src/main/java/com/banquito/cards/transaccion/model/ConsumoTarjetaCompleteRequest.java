package com.banquito.cards.transaccion.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ConsumoTarjetaCompleteRequest {
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
    private DetalleComisiones detalle;

    public static class DetalleComisiones {
        @JsonProperty("gtw")
        private ComisionDetalle gtw;
        @JsonProperty("processor")
        private ComisionDetalle processor;
        @JsonProperty("marca")
        private ComisionDetalle marca;

        public ComisionDetalle getGtw() {
            return gtw;
        }

        public void setGtw(ComisionDetalle gtw) {
            this.gtw = gtw;
        }

        public ComisionDetalle getProcessor() {
            return processor;
        }

        public void setProcessor(ComisionDetalle processor) {
            this.processor = processor;
        }

        public ComisionDetalle getMarca() {
            return marca;
        }

        public void setMarca(ComisionDetalle marca) {
            this.marca = marca;
        }
    }

    public static class ComisionDetalle {
        private BigDecimal comision;
        private String numeroCuenta;

        public BigDecimal getComision() {
            return comision;
        }

        public void setComision(BigDecimal comision) {
            this.comision = comision;
        }

        public String getNumeroCuenta() {
            return numeroCuenta;
        }

        public void setNumeroCuenta(String numeroCuenta) {
            this.numeroCuenta = numeroCuenta;
        }
    }

    // Getters y setters de la clase principal
    public String getNumeroTarjeta() { return numeroTarjeta; }
    public void setNumeroTarjeta(String numeroTarjeta) { this.numeroTarjeta = numeroTarjeta; }
    
    public String getCvv() { return cvv; }
    public void setCvv(String cvv) { this.cvv = cvv; }
    
    public String getFechaCaducidad() { return fechaCaducidad; }
    public void setFechaCaducidad(String fechaCaducidad) { this.fechaCaducidad = fechaCaducidad; }
    
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { 
        this.valor = valor != null ? valor.setScale(2, BigDecimal.ROUND_HALF_UP) : null;
    }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public String getNumeroCuenta() { return numeroCuenta; }
    public void setNumeroCuenta(String numeroCuenta) { this.numeroCuenta = numeroCuenta; }
    
    public Boolean getEsDiferido() { return esDiferido; }
    public void setEsDiferido(Boolean esDiferido) { this.esDiferido = esDiferido; }
    
    public Integer getCuotas() { return cuotas; }
    public void setCuotas(Integer cuotas) { this.cuotas = cuotas; }
    
    public Boolean getInteresDiferido() { return interesDiferido; }
    public void setInteresDiferido(Boolean interesDiferido) { this.interesDiferido = interesDiferido; }
    
    public String getBeneficiario() { return beneficiario; }
    public void setBeneficiario(String beneficiario) { this.beneficiario = beneficiario; }
    
    public DetalleComisiones getDetalle() { return detalle; }
    public void setDetalle(DetalleComisiones detalle) { this.detalle = detalle; }
} 