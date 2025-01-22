package com.banquito.cards.transaccion.controller.mapper;

import com.banquito.cards.transaccion.model.Transaccion;
import com.banquito.cards.transaccion.controller.dto.TransaccionDTO;
import com.banquito.cards.comision.controller.mapper.BancoMapper;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

@Component
@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    uses = {BancoMapper.class}
)
public interface TransaccionMapper {
    
    @Mapping(target = "codigoBanco", source = "banco.codigo")
    @Mapping(target = "banco", source = "banco")
    TransaccionDTO toDTO(Transaccion model);
    
    @InheritInverseConfiguration
    Transaccion toModel(TransaccionDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateModelFromDTO(TransaccionDTO dto, @MappingTarget Transaccion model);
} 