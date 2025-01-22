package com.banquito.cards.fraude.controller.mapper;

import com.banquito.cards.fraude.controller.dto.MonitoreoFraudeDTO;
import com.banquito.cards.fraude.model.MonitoreoFraude;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface MonitoreoFraudeMapper {
    @Mapping(target = "codRegla", source = "reglaFraude.codRegla")
    @Mapping(target = "codTransaccion", source = "transaccion.codigo")
    MonitoreoFraudeDTO toDTO(MonitoreoFraude model);
    
    @Mapping(target = "reglaFraude.codRegla", source = "codRegla")
    @Mapping(target = "transaccion.codigo", source = "codTransaccion")
    MonitoreoFraude toModel(MonitoreoFraudeDTO dto);
} 