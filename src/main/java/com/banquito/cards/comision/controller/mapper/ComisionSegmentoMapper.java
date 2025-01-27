package com.banquito.cards.comision.controller.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import com.banquito.cards.comision.controller.dto.ComisionSegmentoDTO;
import com.banquito.cards.comision.model.ComisionSegmento;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ComisionSegmentoMapper {
    ComisionSegmentoDTO toDTO(ComisionSegmento model);
    ComisionSegmento toModel(ComisionSegmentoDTO dto);
} 