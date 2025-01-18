package com.banquito.cards.transaccion.controller.mapper;

import com.banquito.cards.transaccion.model.HistorialEstadoTransaccion;
import com.banquito.cards.transaccion.controller.dto.HistorialEstadoTransaccionDTO;
import org.springframework.stereotype.Component;

@Component
public class HistorialEstadoTransaccionMapper {
    
    public HistorialEstadoTransaccionDTO toDTO(HistorialEstadoTransaccion model) {
        if (model == null) return null;
        
        return HistorialEstadoTransaccionDTO.builder()
                .codigo(model.getCodigo())
                .codigoTransaccion(model.getTransaccion().getCodigo())
                .estado(model.getEstado())
                .fechaEstadoCambio(model.getFechaEstadoCambio())
                .detalle(model.getDetalle())
                .nombreBanco(model.getTransaccion().getBanco() != null ? 
                    model.getTransaccion().getBanco().getNombreComercial() : null)
                .numeroTarjeta(model.getTransaccion().getNumeroTarjeta())
                .build();
    }
} 