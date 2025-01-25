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
        log.info("Obteniendo historial por estado: {}, fechaInicio: {}, fechaFin: {}, banco: {}",
                estado, fechaInicio, fechaFin, bancoNombre);

        if (fechaInicio == null || fechaFin == null) {
            log.info("Fechas no proporcionadas, usando rango del día actual");
            LocalDateTime ahora = LocalDateTime.now();
            fechaInicio = ahora.toLocalDate().atStartOfDay();
            fechaFin = ahora.toLocalDate().atTime(23, 59, 59);
        }

        if (fechaInicio.isAfter(fechaFin)) {
            log.error("La fecha de inicio es posterior a la fecha fin: fechaInicio={}, fechaFin={}", fechaInicio, fechaFin);
            throw new BusinessException("La fecha de inicio no puede ser posterior a la fecha fin");
        }

        List<HistorialEstadoTransaccion> historial;

        if (estado != null && !estado.trim().isEmpty() && !"ALL".equalsIgnoreCase(estado)) {
            log.debug("Filtrando por estado: {}", estado);
            historial = historialRepository.findByEstadoAndFechaEstadoCambioBetweenOrderByFechaEstadoCambioDesc(
                    estado, fechaInicio, fechaFin);
        } else {
            log.debug("Buscando historial para todas las transacciones en el rango de fechas");
            historial = historialRepository.findByFechaEstadoCambioBetweenOrderByFechaEstadoCambioDesc(fechaInicio, fechaFin);
        }

        if (bancoNombre != null && !bancoNombre.trim().isEmpty()) {
            log.debug("Aplicando filtro por banco: {}", bancoNombre);
            historial = historial.stream()
                    .filter(h -> h.getTransaccion() != null &&
                            h.getTransaccion().getBanco() != null &&
                            h.getTransaccion().getBanco().getNombreComercial() != null &&
                            h.getTransaccion().getBanco().getNombreComercial()
                                    .toLowerCase().contains(bancoNombre.toLowerCase()))
                    .collect(Collectors.toList());
        }

        log.info("Total registros encontrados: {}", historial.size());

        return historial.stream()
                .map(historialMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<HistorialEstadoTransaccionDTO> obtenerHistorialPorFecha(LocalDateTime fecha) {
        log.info("Obteniendo historial por fecha: {}", fecha);

        if (fecha == null) {
            log.error("Fecha no proporcionada para la búsqueda del historial");
            throw new BusinessException("La fecha es requerida");
        }

        LocalDateTime inicioDia = fecha.toLocalDate().atStartOfDay();
        LocalDateTime finDia = fecha.toLocalDate().atTime(23, 59, 59);

        List<HistorialEstadoTransaccionDTO> historial = historialRepository.findByFechaEstadoCambioBetweenOrderByFechaEstadoCambioDesc(
                        inicioDia, finDia)
                .stream()
                .map(historialMapper::toDTO)
                .collect(Collectors.toList());

        log.info("Registros encontrados para la fecha {}: {}", fecha, historial.size());
        return historial;
    }

    @Transactional
    public HistorialEstadoTransaccionDTO registrarCambioEstado(
            Integer transaccionId, String nuevoEstado, String detalle) {
        log.info("Registrando cambio de estado para transacción ID: {}, nuevoEstado: {}, detalle: {}",
                transaccionId, nuevoEstado, detalle);

        Transaccion transaccion = transaccionRepository.findById(transaccionId)
                .orElseThrow(() -> {
                    log.error("Transacción no encontrada con ID: {}", transaccionId);
                    return new NotFoundException(transaccionId.toString(), "Transaccion");
                });

        validarTransicionEstado(transaccion.getEstado(), nuevoEstado);

        HistorialEstadoTransaccion historial = new HistorialEstadoTransaccion();
        historial.setTransaccion(transaccion);
        historial.setEstado(nuevoEstado);
        historial.setFechaEstadoCambio(LocalDateTime.now());
        historial.setDetalle(detalle);

        transaccion.setEstado(nuevoEstado);
        transaccion.setDetalle(detalle);

        historialRepository.save(historial);
        transaccionRepository.save(transaccion);

        log.info("Cambio de estado registrado exitosamente para transacción ID: {}", transaccionId);
        return historialMapper.toDTO(historial);
    }

    private void validarTransicionEstado(String estadoActual, String nuevoEstado) {
        log.debug("Validando transición de estado: {} -> {}", estadoActual, nuevoEstado);

        if ("REC".equals(estadoActual)) {
            log.error("No se puede cambiar el estado de una transacción rechazada");
            throw new BusinessException("No se pueden realizar cambios en una transacción rechazada");
        }

        if ("APR".equals(estadoActual) && !"REV".equals(nuevoEstado)) {
            log.error("Una transacción aprobada solo puede ser reversada");
            throw new BusinessException("Una transacción aprobada solo puede ser reversada");
        }

        if ("PEN".equals(estadoActual) &&
                !"APR".equals(nuevoEstado) &&
                !"REC".equals(nuevoEstado)) {
            log.error("Transición no válida para una transacción pendiente: {}", nuevoEstado);
            throw new BusinessException("Una transacción pendiente solo puede ser aprobada o rechazada");
        }

        log.debug("Transición de estado válida: {} -> {}", estadoActual, nuevoEstado);
    }
}
