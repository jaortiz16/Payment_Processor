package com.banquito.cards.transaccion.controller.mapper;

import com.banquito.cards.transaccion.controller.dto.TransaccionDTO;
import com.banquito.cards.transaccion.model.Transaccion;
import org.springframework.stereotype.Component;

@Component
public class TransaccionMapper {

    public TransaccionDTO toDTO(Transaccion transaccion) {
        if (transaccion == null) return null;
        
        return TransaccionDTO.builder()
                .codigo(transaccion.getCodigo())
                .codigoBanco(transaccion.getBanco() != null ? transaccion.getBanco().getCodigo() : null)
                .codigoComision(transaccion.getComision() != null ? transaccion.getComision().getCodigo() : null)
                .nombreBanco(transaccion.getBanco() != null ? transaccion.getBanco().getNombreComercial() : null)
                .nombreComision(transaccion.getComision() != null ? transaccion.getComision().getTipo() : null)
                .monto(transaccion.getMonto())
                .modalidad(transaccion.getModalidad())
                .codigoMoneda(transaccion.getCodigoMoneda())
                .marca(transaccion.getMarca())
                .fechaExpiracionTarjeta(transaccion.getFechaExpiracionTarjeta())
                .nombreTarjeta(transaccion.getNombreTarjeta())
                .numeroTarjeta(transaccion.getNumeroTarjeta())
                .direccionTarjeta(transaccion.getDireccionTarjeta())
                .cvv(transaccion.getCvv())
                .pais(transaccion.getPais())
                .estado(transaccion.getEstado())
                .detalle(transaccion.getDetalle())
                .codigoUnicoTransaccion(transaccion.getCodigoUnicoTransaccion())
                .fechaCreacion(transaccion.getFechaCreacion())
                .fechaEjecucionRecurrencia(transaccion.getFechaEjecucionRecurrencia())
                .fechaFinRecurrencia(transaccion.getFechaFinRecurrencia())
                .gtwComision(transaccion.getGtwComision())
                .gtwCuenta(transaccion.getGtwCuenta())
                .numeroCuenta(transaccion.getNumeroCuenta())
                .cuotas(transaccion.getCuotas())
                .interesDiferido(transaccion.getInteresDiferido())
                .beneficiario(transaccion.getBeneficiario())
                .build();
    }

    public void updateModelFromDTO(TransaccionDTO dto, Transaccion transaccion) {
        if (dto == null || transaccion == null) return;
        
        if (dto.getMonto() != null) transaccion.setMonto(dto.getMonto());
        if (dto.getModalidad() != null) transaccion.setModalidad(dto.getModalidad());
        if (dto.getCodigoMoneda() != null) transaccion.setCodigoMoneda(dto.getCodigoMoneda());
        if (dto.getMarca() != null) transaccion.setMarca(dto.getMarca());
        if (dto.getFechaExpiracionTarjeta() != null) transaccion.setFechaExpiracionTarjeta(dto.getFechaExpiracionTarjeta());
        if (dto.getNombreTarjeta() != null) transaccion.setNombreTarjeta(dto.getNombreTarjeta());
        if (dto.getNumeroTarjeta() != null) transaccion.setNumeroTarjeta(dto.getNumeroTarjeta());
        if (dto.getDireccionTarjeta() != null) transaccion.setDireccionTarjeta(dto.getDireccionTarjeta());
        if (dto.getCvv() != null) transaccion.setCvv(dto.getCvv());
        if (dto.getPais() != null) transaccion.setPais(dto.getPais());
        if (dto.getEstado() != null) transaccion.setEstado(dto.getEstado());
        if (dto.getDetalle() != null) transaccion.setDetalle(dto.getDetalle());
        if (dto.getCodigoUnicoTransaccion() != null) transaccion.setCodigoUnicoTransaccion(dto.getCodigoUnicoTransaccion());
        if (dto.getFechaEjecucionRecurrencia() != null) transaccion.setFechaEjecucionRecurrencia(dto.getFechaEjecucionRecurrencia());
        if (dto.getFechaFinRecurrencia() != null) transaccion.setFechaFinRecurrencia(dto.getFechaFinRecurrencia());
        if (dto.getGtwComision() != null) transaccion.setGtwComision(dto.getGtwComision());
        if (dto.getGtwCuenta() != null) transaccion.setGtwCuenta(dto.getGtwCuenta());
        if (dto.getNumeroCuenta() != null) transaccion.setNumeroCuenta(dto.getNumeroCuenta());
        if (dto.getCuotas() != null) transaccion.setCuotas(dto.getCuotas());
        if (dto.getInteresDiferido() != null) transaccion.setInteresDiferido(dto.getInteresDiferido());
        if (dto.getBeneficiario() != null) transaccion.setBeneficiario(dto.getBeneficiario());
    }

    public Transaccion toModel(TransaccionDTO dto) {
        if (dto == null) return null;
        Transaccion model = new Transaccion();
        model.setCodigo(dto.getCodigo());
        model.setCodigoUnicoTransaccion(dto.getCodigoUnicoTransaccion());
        model.setEstado(dto.getEstado());
        model.setFechaCreacion(dto.getFechaCreacion());
        return model;
    }
} 