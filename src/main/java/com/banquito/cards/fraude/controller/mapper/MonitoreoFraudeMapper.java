package com.banquito.cards.fraude.controller.mapper;

import com.banquito.cards.fraude.controller.dto.MonitoreoFraudeDTO;
import com.banquito.cards.fraude.model.MonitoreoFraude;
import org.springframework.stereotype.Component;

@Component
public class MonitoreoFraudeMapper {

    public MonitoreoFraudeDTO toDTO(MonitoreoFraude monitoreo) {
        if (monitoreo == null) return null;
        
        return MonitoreoFraudeDTO.builder()
                .codigo(monitoreo.getCodigo())
                .codRegla(monitoreo.getReglaFraude() != null ? monitoreo.getReglaFraude().getCodRegla() : null)
                .codTransaccion(monitoreo.getTransaccion() != null ? monitoreo.getTransaccion().getCodigo() : null)
                .nivelRiesgo(monitoreo.getNivelRiesgo())
                .puntajeRiesgo(monitoreo.getPuntajeRiesgo())
                .estado(monitoreo.getEstado())
                .detalle(monitoreo.getDetalle())
                .accionTomada(monitoreo.getAccionTomada())
                .requiereVerificacionAdicional(monitoreo.getRequiereVerificacionAdicional())
                .motivoVerificacion(monitoreo.getMotivoVerificacion())
                .fechaDeteccion(monitoreo.getFechaDeteccion())
                .fechaProcesamiento(monitoreo.getFechaProcesamiento())
                .codigoUnicoTransaccion(monitoreo.getCodigoUnicoTransaccion())
                .usuarioProcesamiento(monitoreo.getUsuarioProcesamiento())
                .ipOrigen(monitoreo.getIpOrigen())
                .ubicacionGeografica(monitoreo.getUbicacionGeografica())
                .build();
    }

    public void updateModelFromDTO(MonitoreoFraudeDTO dto, MonitoreoFraude monitoreo) {
        if (dto == null || monitoreo == null) return;
        
        monitoreo.setEstado(dto.getEstado());
        monitoreo.setDetalle(dto.getDetalle());
        monitoreo.setAccionTomada(dto.getAccionTomada());
        monitoreo.setRequiereVerificacionAdicional(dto.getRequiereVerificacionAdicional());
        monitoreo.setMotivoVerificacion(dto.getMotivoVerificacion());
        monitoreo.setUsuarioProcesamiento(dto.getUsuarioProcesamiento());
        monitoreo.setIpOrigen(dto.getIpOrigen());
        monitoreo.setUbicacionGeografica(dto.getUbicacionGeografica());
    }
} 