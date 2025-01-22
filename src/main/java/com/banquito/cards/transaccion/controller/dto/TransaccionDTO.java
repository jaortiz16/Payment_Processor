package com.banquito.cards.transaccion.controller.dto;

import com.banquito.cards.comision.controller.dto.BancoDTO;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransaccionDTO {
    private Integer codigo;
    private Integer codigoBanco;
    private String numeroTarjeta;
    private String marca;
    private String modalidad;
    private String codigoMoneda;
    private BigDecimal monto;
    private String estado;
    private String detalle;
    private String codigoUnicoTransaccion;
    private String pais;
    private String fechaExpiracionTarjeta;
    private String nombreTarjeta;
    private String direccionTarjeta;
    private String cvv;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaEjecucionRecurrencia;
    private LocalDateTime fechaFinRecurrencia;
    private Integer cuotas;
    private String gtwComision;
    private String gtwCuenta;
    private String numeroCuenta;
    private Boolean interesDiferido;
    private String beneficiario;
    private BancoDTO banco;
} 