package com.banquito.cards.transaccion.controller.mapper;

import com.banquito.cards.transaccion.controller.dto.HistorialEstadoTransaccionDTO;
import com.banquito.cards.transaccion.model.HistorialEstadoTransaccion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface HistorialEstadoTransaccionMapper {
    
    @Mapping(target = "codigoTransaccion", source = "transaccion.codigo")
    HistorialEstadoTransaccionDTO toDTO(HistorialEstadoTransaccion model);
    
    @Mapping(target = "transaccion.codigo", source = "codigoTransaccion")
    HistorialEstadoTransaccion toModel(HistorialEstadoTransaccionDTO dto);
} 