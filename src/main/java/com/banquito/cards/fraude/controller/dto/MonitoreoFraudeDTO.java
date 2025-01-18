package com.banquito.cards.fraude.controller.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class MonitoreoFraudeDTO {
    private Integer codigo;
    private Integer codRegla;
    private Integer codTransaccion;
    private String nivelRiesgo;
    private BigDecimal puntajeRiesgo;
    private String estado;
    private String detalle;
    private String accionTomada;
    private Boolean requiereVerificacionAdicional;
    private String motivoVerificacion;
    private LocalDateTime fechaDeteccion;
    private LocalDateTime fechaProcesamiento;
    private String codigoUnicoTransaccion;
    private String usuarioProcesamiento;
    private String ipOrigen;
    private String ubicacionGeografica;
} 