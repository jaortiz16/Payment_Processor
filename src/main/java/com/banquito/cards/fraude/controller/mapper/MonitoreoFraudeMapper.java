package com.banquito.cards.fraude.controller.mapper;

import com.banquito.cards.fraude.controller.dto.MonitoreoFraudeDTO;
import com.banquito.cards.fraude.model.MonitoreoFraude;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface MonitoreoFraudeMapper {
    MonitoreoFraudeDTO toDTO(MonitoreoFraude model);
    MonitoreoFraude toModel(MonitoreoFraudeDTO dto);
} 