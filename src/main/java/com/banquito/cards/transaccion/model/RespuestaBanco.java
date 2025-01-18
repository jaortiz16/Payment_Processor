package com.banquito.cards.transaccion.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RespuestaBanco {
    private boolean success;
    private String message;
    private String codigoAutorizacion;
    private String codigoReferencia;
    private String estado;
    private BigDecimal montoAutorizado;
    private String numeroCuenta;
    private String nombreTarjetahabiente;
    private String codigoError;
    private String descripcionError;
    private String codigoUnicoTransaccion;
    private String fechaProcesamiento;
    private String horaTransaccion;
    private String codigoComercio;
    private String nombreComercio;
    private String direccionComercio;
    private String ciudadComercio;
    private String paisComercio;
    private String mcc;
    private String moneda;
    private String terminal;
    private String tipoTransaccion;
} 