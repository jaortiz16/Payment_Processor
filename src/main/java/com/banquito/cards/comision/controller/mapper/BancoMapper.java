package com.banquito.cards.comision.controller.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import com.banquito.cards.comision.controller.dto.BancoDTO;
import com.banquito.cards.comision.model.Banco;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface BancoMapper {
    @Mapping(target = "codComision", source = "comision.codigo")
    BancoDTO toDTO(Banco model);
    
    @Mapping(target = "comision.codigo", source = "codComision")
    Banco toModel(BancoDTO dto);
} 