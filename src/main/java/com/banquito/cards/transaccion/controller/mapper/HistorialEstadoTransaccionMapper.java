package com.banquito.cards.transaccion.controller.mapper;

import com.banquito.cards.transaccion.model.HistorialEstadoTransaccion;
import com.banquito.cards.transaccion.controller.dto.HistorialEstadoTransaccionDTO;
import org.mapstruct.*;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    uses = {TransaccionMapper.class}
)
public interface HistorialEstadoTransaccionMapper {
    
    @Mapping(target = "codigoTransaccion", source = "transaccion.codigo")
    @Mapping(target = "transaccion", source = "transaccion")
    HistorialEstadoTransaccionDTO toDTO(HistorialEstadoTransaccion model);
    
    @InheritInverseConfiguration
    HistorialEstadoTransaccion toModel(HistorialEstadoTransaccionDTO dto);
} 