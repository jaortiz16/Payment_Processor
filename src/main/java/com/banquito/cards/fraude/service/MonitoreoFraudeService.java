package com.banquito.cards.fraude.service;

import com.banquito.cards.fraude.model.MonitoreoFraude;
import com.banquito.cards.fraude.repository.MonitoreoFraudeRepository;
import com.banquito.cards.fraude.controller.dto.MonitoreoFraudeDTO;
import com.banquito.cards.fraude.controller.mapper.MonitoreoFraudeMapper;
import com.banquito.cards.exception.NotFoundException;
import com.banquito.cards.exception.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MonitoreoFraudeService {
    
    private static final String ENTITY_NAME = "MonitoreoFraude";
    
    private final MonitoreoFraudeRepository monitoreoFraudeRepository;
    private final MonitoreoFraudeMapper monitoreoFraudeMapper;

    public MonitoreoFraudeService(MonitoreoFraudeRepository monitoreoFraudeRepository,
                                 MonitoreoFraudeMapper monitoreoFraudeMapper) {
        this.monitoreoFraudeRepository = monitoreoFraudeRepository;
        this.monitoreoFraudeMapper = monitoreoFraudeMapper;
    }

    @Transactional
    public void actualizarEstadoMonitoreoFraude(String codigoUnicoTransaccion, String estado) {
        MonitoreoFraude monitoreo = monitoreoFraudeRepository
                .findFirstByCodigoUnicoTransaccionOrderByFechaDeteccionDesc(codigoUnicoTransaccion)
                .orElseThrow(() -> new NotFoundException(codigoUnicoTransaccion, ENTITY_NAME));
                
        if (!List.of(MonitoreoFraude.ESTADO_PENDIENTE, MonitoreoFraude.ESTADO_PROCESADO,
                MonitoreoFraude.ESTADO_RECHAZADO, MonitoreoFraude.ESTADO_APROBADO,
                MonitoreoFraude.ESTADO_EN_REVISION).contains(estado)) {
            throw new BusinessException("Estado no válido para monitoreo de fraude");
        }
        
        monitoreo.setEstado(estado);
        monitoreo.setFechaProcesamiento(LocalDateTime.now());
        monitoreoFraudeRepository.save(monitoreo);
    }

    @Transactional(readOnly = true)
    public List<MonitoreoFraude> obtenerAlertasPendientes() {
        return monitoreoFraudeRepository.findByEstado(MonitoreoFraude.ESTADO_PENDIENTE);
    }

    @Transactional(readOnly = true)
    public List<MonitoreoFraude> obtenerAlertasPorFecha(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        if (fechaInicio == null || fechaFin == null) {
            throw new BusinessException("Las fechas de inicio y fin son requeridas");
        }
        if (fechaInicio.isAfter(fechaFin)) {
            throw new BusinessException("La fecha de inicio no puede ser posterior a la fecha fin");
        }
        return monitoreoFraudeRepository.findByFechaDeteccionBetween(fechaInicio, fechaFin);
    }

    @Transactional(readOnly = true)
    public List<MonitoreoFraude> obtenerAlertasPorTransaccion(Integer codTransaccion) {
        return monitoreoFraudeRepository.findByTransaccionCodigo(codTransaccion);
    }

    @Transactional
    public void procesarAlerta(Integer id, String estado, String detalle, String accionTomada, 
            Boolean requiereVerificacion, String motivoVerificacion) {
        MonitoreoFraude monitoreo = monitoreoFraudeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(id.toString(), ENTITY_NAME));
        
        monitoreo.setEstado(estado);
        monitoreo.setDetalle(detalle);
        monitoreo.setAccionTomada(accionTomada);
        monitoreo.setRequiereVerificacionAdicional(requiereVerificacion);
        monitoreo.setMotivoVerificacion(motivoVerificacion);
        monitoreo.setFechaProcesamiento(LocalDateTime.now());
        
        monitoreoFraudeRepository.save(monitoreo);
    }

    @Transactional(readOnly = true)
    public Optional<MonitoreoFraude> obtenerAlertaPorId(Integer id) {
        return monitoreoFraudeRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<MonitoreoFraude> obtenerAlertasPorTarjeta(String numeroTarjeta, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        if (fechaInicio == null || fechaFin == null) {
            throw new BusinessException("Las fechas de inicio y fin son requeridas");
        }
        return monitoreoFraudeRepository.findByTransaccionNumeroTarjetaAndFechaDeteccionBetween(
            numeroTarjeta, fechaInicio, fechaFin);
    }

    @Transactional
    public MonitoreoFraude crearMonitoreo(MonitoreoFraude monitoreo) {
        monitoreo.setFechaDeteccion(LocalDateTime.now());
        monitoreo.setEstado(MonitoreoFraude.ESTADO_PENDIENTE);
        return monitoreoFraudeRepository.save(monitoreo);
    }
}
