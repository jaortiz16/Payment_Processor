package com.banquito.cards.fraude.controller.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MonitoreoFraudeDTO {
    private String codigo;
    private Integer codRegla;
    private Integer codTransaccion;
    private String nivelRiesgo;
    private Integer puntajeRiesgo;
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