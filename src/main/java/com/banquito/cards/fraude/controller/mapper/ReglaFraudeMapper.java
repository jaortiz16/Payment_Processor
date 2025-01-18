package com.banquito.cards.fraude.controller.mapper;

import com.banquito.cards.fraude.controller.dto.ReglaFraudeDTO;
import com.banquito.cards.fraude.model.ReglaFraude;
import org.springframework.stereotype.Component;

@Component
public class ReglaFraudeMapper {

    public ReglaFraudeDTO toDTO(ReglaFraude regla) {
        if (regla == null) return null;
        
        return ReglaFraudeDTO.builder()
                .codRegla(regla.getCodRegla())
                .nombreRegla(regla.getNombreRegla())
                .descripcion(regla.getDescripcion())
                .tipoRegla(regla.getTipoRegla())
                .limiteTransacciones(regla.getLimiteTransacciones())
                .periodoTiempo(regla.getPeriodoTiempo())
                .limiteMontoTotal(regla.getLimiteMontoTotal())
                .paisesPermitidos(regla.getPaisesPermitidos())
                .comerciosExcluidos(regla.getComerciosExcluidos())
                .horaInicio(regla.getHoraInicio())
                .horaFin(regla.getHoraFin())
                .puntajeRiesgo(regla.getPuntajeRiesgo())
                .nivelRiesgo(regla.getNivelRiesgo())
                .estado(regla.getEstado())
                .prioridad(regla.getPrioridad())
                .fechaCreacion(regla.getFechaCreacion())
                .fechaActualizacion(regla.getFechaActualizacion())
                .usuarioCreacion(regla.getUsuarioCreacion())
                .usuarioActualizacion(regla.getUsuarioActualizacion())
                .build();
    }

    public ReglaFraude toModel(ReglaFraudeDTO dto) {
        if (dto == null) return null;
        
        ReglaFraude regla = new ReglaFraude();
        regla.setCodRegla(dto.getCodRegla());
        regla.setNombreRegla(dto.getNombreRegla());
        regla.setDescripcion(dto.getDescripcion());
        regla.setTipoRegla(dto.getTipoRegla());
        regla.setLimiteTransacciones(dto.getLimiteTransacciones());
        regla.setPeriodoTiempo(dto.getPeriodoTiempo());
        regla.setLimiteMontoTotal(dto.getLimiteMontoTotal());
        regla.setPaisesPermitidos(dto.getPaisesPermitidos());
        regla.setComerciosExcluidos(dto.getComerciosExcluidos());
        regla.setHoraInicio(dto.getHoraInicio());
        regla.setHoraFin(dto.getHoraFin());
        regla.setPuntajeRiesgo(dto.getPuntajeRiesgo());
        regla.setNivelRiesgo(dto.getNivelRiesgo());
        regla.setEstado(dto.getEstado());
        regla.setPrioridad(dto.getPrioridad());
        regla.setFechaCreacion(dto.getFechaCreacion());
        regla.setFechaActualizacion(dto.getFechaActualizacion());
        regla.setUsuarioCreacion(dto.getUsuarioCreacion());
        regla.setUsuarioActualizacion(dto.getUsuarioActualizacion());
        
        return regla;
    }
} 