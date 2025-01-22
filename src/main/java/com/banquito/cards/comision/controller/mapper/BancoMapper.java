package com.banquito.cards.comision.controller.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.AfterMapping;
import org.mapstruct.MappingTarget;

import com.banquito.cards.comision.controller.dto.BancoDTO;
import com.banquito.cards.comision.model.Banco;
import com.banquito.cards.comision.model.Comision;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface BancoMapper {
    @Mapping(target = "comision.codigo", source = "comision.codigo")
    @Mapping(target = "comision.nombre", source = "comision.tipo")
    @Mapping(target = "comision.porcentaje", source = "comision.montoBase")
    BancoDTO toDTO(Banco model);
    
    @Mapping(target = "comision", ignore = true)
    Banco toModel(BancoDTO dto);

    @AfterMapping
    default void afterMapping(@MappingTarget Banco banco, BancoDTO dto) {
        if (dto.getCodComision() != null) {
            banco.setComision(new Comision(dto.getCodComision()));
        } else if (dto.getComision() != null && dto.getComision().getCodigo() != null) {
            banco.setComision(new Comision(dto.getComision().getCodigo()));
        }
    }
} 