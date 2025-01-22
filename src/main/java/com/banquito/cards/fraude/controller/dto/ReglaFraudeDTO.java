package com.banquito.cards.fraude.controller.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ReglaFraudeDTO {
    private Integer codRegla;
    private String nombreRegla;
    private String descripcion;
    private String tipoRegla;
    private BigDecimal limiteTransacciones;
    private String periodoTiempo;
    private BigDecimal limiteMontoTotal;
    private String paisesPermitidos;
    private String comerciosExcluidos;
    private LocalDateTime horaInicio;
    private LocalDateTime horaFin;
    private BigDecimal puntajeRiesgo;
    private String nivelRiesgo;
    private String estado;
    private Integer prioridad;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    private String usuarioCreacion;
    private String usuarioActualizacion;
} 