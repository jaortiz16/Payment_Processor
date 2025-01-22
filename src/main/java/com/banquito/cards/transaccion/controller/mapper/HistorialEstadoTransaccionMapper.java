package com.banquito.cards.transaccion.controller.mapper;

import com.banquito.cards.transaccion.model.HistorialEstadoTransaccion;
import com.banquito.cards.transaccion.model.Transaccion;
import com.banquito.cards.transaccion.controller.dto.HistorialEstadoTransaccionDTO;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

@Component
@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    uses = {TransaccionMapper.class}
)
public interface HistorialEstadoTransaccionMapper {
    
    @Mapping(target = "codTransaccion", source = "transaccion.codigo")
    @Mapping(target = "transaccion", source = "transaccion")
    HistorialEstadoTransaccionDTO toDTO(HistorialEstadoTransaccion model);
    
    @InheritInverseConfiguration
    HistorialEstadoTransaccion toModel(HistorialEstadoTransaccionDTO dto);
} 