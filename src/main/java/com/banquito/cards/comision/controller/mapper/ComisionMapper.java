package com.banquito.cards.comision.controller.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import com.banquito.cards.comision.controller.dto.ComisionDTO;
import com.banquito.cards.comision.model.Comision;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ComisionMapper {
    ComisionDTO toDTO(Comision model);
    Comision toModel(ComisionDTO dto);
} 