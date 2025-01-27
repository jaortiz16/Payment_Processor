package com.banquito.cards.transaccion.service;

import com.banquito.cards.transaccion.model.HistorialEstadoTransaccion;
import com.banquito.cards.transaccion.model.Transaccion;
import com.banquito.cards.transaccion.repository.HistorialEstadoTransaccionRepository;
import com.banquito.cards.transaccion.repository.TransaccionRepository;
import com.banquito.cards.transaccion.controller.dto.HistorialEstadoTransaccionDTO;
import com.banquito.cards.transaccion.controller.mapper.HistorialEstadoTransaccionMapper;
import com.banquito.cards.exception.BusinessException;
import com.banquito.cards.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class HistorialEstadoTransaccionService {

    private static final String ENTITY_NAME = "HistorialEstadoTransaccion";
    
    private final HistorialEstadoTransaccionRepository historialRepository;
    private final TransaccionRepository transaccionRepository;
    private final HistorialEstadoTransaccionMapper historialMapper;

    public HistorialEstadoTransaccionService(
            HistorialEstadoTransaccionRepository historialRepository,
            TransaccionRepository transaccionRepository,
            HistorialEstadoTransaccionMapper historialMapper) {
        this.historialRepository = historialRepository;
        this.transaccionRepository = transaccionRepository;
        this.historialMapper = historialMapper;
    }

    @Transactional(readOnly = true)
    public List<HistorialEstadoTransaccionDTO> obtenerHistorialPorFechaYEstado(
            String estado, LocalDateTime fechaInicio, LocalDateTime fechaFin, String bancoNombre) {
        log.debug("Obteniendo historial por fecha y estado: estado={}, fechaInicio={}, fechaFin={}, banco={}", 
            estado, fechaInicio, fechaFin, bancoNombre);
            
        if (fechaInicio == null || fechaFin == null) {
            log.debug("Fechas no proporcionadas, usando día actual");
            LocalDateTime ahora = LocalDateTime.now();
            fechaInicio = ahora.toLocalDate().atStartOfDay();
            fechaFin = ahora.toLocalDate().atTime(23, 59, 59);
        }
        if (fechaInicio.isAfter(fechaFin)) {
            throw new BusinessException("fechas", ENTITY_NAME, "validar fechas");
        }

        List<HistorialEstadoTransaccion> historial;
        if (estado != null && !estado.trim().isEmpty() && !estado.equals("ALL")) {
            log.debug("Buscando por estado: {}", estado);
            historial = historialRepository.findByEstadoAndFechaEstadoCambioBetweenOrderByFechaEstadoCambioDesc(
                estado, fechaInicio, fechaFin);
        } else {
            log.debug("Buscando todas las transacciones en el rango de fechas");
            historial = historialRepository.findByFechaEstadoCambioBetweenOrderByFechaEstadoCambioDesc(
                fechaInicio, fechaFin);
        }

        log.debug("Registros encontrados: {}", historial.size());

        // Filtrar por nombre de banco si se proporciona
        if (bancoNombre != null && !bancoNombre.trim().isEmpty()) {
            log.debug("Filtrando por banco: {}", bancoNombre);
            historial = historial.stream()
                .filter(h -> h.getTransaccion() != null && 
                           h.getTransaccion().getBanco() != null && 
                           h.getTransaccion().getBanco().getNombreComercial() != null &&
                           h.getTransaccion().getBanco().getNombreComercial()
                               .toLowerCase()
                               .contains(bancoNombre.toLowerCase()))
                .collect(Collectors.toList());
            log.debug("Registros después del filtro por banco: {}", historial.size());
        }

        List<HistorialEstadoTransaccionDTO> dtos = historial.stream()
            .map(historialMapper::toDTO)
            .collect(Collectors.toList());
            
        log.debug("DTOs generados: {}", dtos.size());
        return dtos;
    }

    @Transactional(readOnly = true)
    public List<HistorialEstadoTransaccionDTO> obtenerHistorialPorFecha(LocalDateTime fecha) {
        if (fecha == null) {
            throw new BusinessException("fecha", ENTITY_NAME, "validar fecha");
        }

        LocalDateTime inicioDia = fecha.toLocalDate().atStartOfDay();
        LocalDateTime finDia = fecha.toLocalDate().atTime(23, 59, 59);

        return historialRepository.findByFechaEstadoCambioBetweenOrderByFechaEstadoCambioDesc(inicioDia, finDia)
            .stream()
            .map(historialMapper::toDTO)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<HistorialEstadoTransaccionDTO> listarHistorial(
            String estado, LocalDateTime fechaInicio, LocalDateTime fechaFin, Pageable pageable) {
        log.debug("Listando historial con filtros: estado={}, fechaInicio={}, fechaFin={}", 
            estado, fechaInicio, fechaFin);
            
        if (fechaInicio == null || fechaFin == null) {
            log.debug("Fechas no proporcionadas, usando día actual");
            LocalDateTime ahora = LocalDateTime.now();
            fechaInicio = ahora.toLocalDate().atStartOfDay();
            fechaFin = ahora.toLocalDate().atTime(23, 59, 59);
        }

        if (fechaInicio.isAfter(fechaFin)) {
            throw new BusinessException("fechas", ENTITY_NAME, "validar fechas");
        }

        Page<HistorialEstadoTransaccion> historial;
        if (estado != null && !estado.trim().isEmpty()) {
            log.debug("Buscando por estado: {}", estado);
            historial = historialRepository.findByEstadoAndFechaEstadoCambioBetween(
                estado, fechaInicio, fechaFin, pageable);
        } else {
            log.debug("Buscando todas las transacciones en el rango de fechas");
            historial = historialRepository.findByFechaEstadoCambioBetween(
                fechaInicio, fechaFin, pageable);
        }

        log.debug("Registros encontrados: {}", historial.getTotalElements());
        return historial.map(historialMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<HistorialEstadoTransaccionDTO> obtenerHistorialPorTransaccion(
            Integer codigoTransaccion, String estado, Pageable pageable) {
        log.info("Obteniendo historial para transacción {} con estado {}", codigoTransaccion, estado);
        Transaccion transaccion = transaccionRepository.findById(codigoTransaccion)
                .orElseThrow(() -> new NotFoundException(codigoTransaccion.toString(), "Transaccion"));

        Page<HistorialEstadoTransaccion> historial;
        if (estado != null && !estado.isEmpty()) {
            historial = historialRepository.findByTransaccionAndEstado(transaccion, estado, pageable);
        } else {
            historial = historialRepository.findByTransaccion(transaccion, pageable);
        }

        return historial.map(historialMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public HistorialEstadoTransaccionDTO obtenerEstadoPorId(Integer id) {
        log.info("Obteniendo estado por ID: {}", id);
        return historialRepository.findById(id)
                .map(historialMapper::toDTO)
                .orElseThrow(() -> new NotFoundException(id.toString(), "HistorialEstadoTransaccion"));
    }

    @Transactional
    public HistorialEstadoTransaccionDTO registrarCambioEstado(Integer codigoTransaccion, String estado, String detalle) {
        log.info("Registrando cambio de estado {} para transacción {}", estado, codigoTransaccion);
        Transaccion transaccion = transaccionRepository.findById(codigoTransaccion)
                .orElseThrow(() -> new NotFoundException(codigoTransaccion.toString(), "Transaccion"));

        validarTransicionEstado(transaccion.getEstado(), estado);

        HistorialEstadoTransaccion historial = new HistorialEstadoTransaccion();
        historial.setTransaccion(transaccion);
        historial.setEstado(estado);
        historial.setFechaEstadoCambio(LocalDateTime.now());
        historial.setDetalle(detalle);
        transaccion.setEstado(estado);
        transaccion.setDetalle(detalle);
        transaccionRepository.save(transaccion);

        return historialMapper.toDTO(historialRepository.save(historial));
    }

    private void validarTransicionEstado(String estadoActual, String nuevoEstado) {
        if ("REC".equals(estadoActual)) {
            throw new BusinessException(estadoActual, ENTITY_NAME, "validar transición estado");
        }
        
        if ("APR".equals(estadoActual) && !"REV".equals(nuevoEstado)) {
            throw new BusinessException(estadoActual, ENTITY_NAME, "validar transición estado");
        }
        
        if ("PEN".equals(estadoActual) && 
            !"APR".equals(nuevoEstado) && 
            !"REC".equals(nuevoEstado)) {
            throw new BusinessException(estadoActual, ENTITY_NAME, "validar transición estado");
        }
    }
}
