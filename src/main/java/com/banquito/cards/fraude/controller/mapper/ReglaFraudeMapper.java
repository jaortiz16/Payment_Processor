package com.banquito.cards.fraude.controller.mapper;

import com.banquito.cards.fraude.controller.dto.ReglaFraudeDTO;
import com.banquito.cards.fraude.model.ReglaFraude;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;


@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ReglaFraudeMapper {
    ReglaFraudeDTO toDTO(ReglaFraude model);
    ReglaFraude toModel(ReglaFraudeDTO dto);
} 