package com.banquito.cards.transaccion.controller.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class TransaccionDTO {
    private Integer codigo;
    private Integer codigoBanco;
    private Integer codigoComision;
    private BigDecimal monto;
    private String modalidad;
    private String codigoMoneda;
    private String marca;
    private String fechaExpiracionTarjeta;
    private String nombreTarjeta;
    private String numeroTarjeta;
    private String direccionTarjeta;
    private String cvv;
    private String pais;
    private String estado;
    private String detalle;
    private String codigoUnicoTransaccion;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaEjecucionRecurrencia;
    private LocalDateTime fechaFinRecurrencia;
    private String gtwComision;
    private String gtwCuenta;
    private String numeroCuenta;
    private Integer cuotas;
    private Boolean interesDiferido;
    private String beneficiario;
    private String nombreBanco;
    private String nombreComision;
} 