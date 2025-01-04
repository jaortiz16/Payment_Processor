package com.banquito.cards.transaccion.service;

import com.banquito.cards.transaccion.model.Transaccion;
import com.banquito.cards.transaccion.model.HistorialEstadoTransaccion;
import com.banquito.cards.transaccion.repository.HistorialEstadoTransaccionRepository;
import com.banquito.cards.transaccion.repository.TransaccionRepository;
import com.banquito.cards.fraude.service.MonitoreoFraudeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;

@Service
@Transactional
public class TransaccionService {

    public static final String ESTADO_PENDIENTE = "PEN";
    public static final String ESTADO_APROBADA = "APR";
    public static final String ESTADO_RECHAZADA = "REC";
    public static final String ESTADO_REVISION = "REV";

    private final TransaccionRepository transaccionRepository;
    private final HistorialEstadoTransaccionRepository historialRepository;
    private final MonitoreoFraudeService monitoreoFraudeService;

    public TransaccionService(TransaccionRepository transaccionRepository,
                            HistorialEstadoTransaccionRepository historialRepository,
                            MonitoreoFraudeService monitoreoFraudeService) {
        this.transaccionRepository = transaccionRepository;
        this.historialRepository = historialRepository;
        this.monitoreoFraudeService = monitoreoFraudeService;
    }

    @Transactional(readOnly = true)
    public List<Transaccion> obtenerTransaccionesPorEstadoYFecha(String estado, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        if (fechaInicio == null || fechaFin == null) {
            throw new RuntimeException("Las fechas de inicio y fin son requeridas");
        }
        if (fechaInicio.isAfter(fechaFin)) {
            throw new RuntimeException("La fecha de inicio no puede ser posterior a la fecha fin");
        }
        return this.transaccionRepository.findByEstadoAndFechaCreacionBetween(estado, fechaInicio, fechaFin);
    }

    @Transactional(readOnly = true)
    public Transaccion obtenerTransaccionPorId(Integer id) {
        return this.transaccionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No existe la transacción con id: " + id));
    }

    @Transactional(readOnly = true)
    public List<Transaccion> obtenerTransaccionesPorBancoYMonto(Integer codigoBanco, BigDecimal montoMinimo, BigDecimal montoMaximo) {
        if (montoMinimo != null && montoMaximo != null && montoMinimo.compareTo(montoMaximo) > 0) {
            throw new RuntimeException("El monto mínimo no puede ser mayor al monto máximo");
        }
        return this.transaccionRepository.findByBancoCodigoAndMontoBetween(codigoBanco, montoMinimo, montoMaximo);
    }

    public Transaccion crearTransaccion(Transaccion transaccion) {
        try {
            validarTransaccion(transaccion);
            transaccion.setFechaCreacion(LocalDateTime.now());
            transaccion.setEstado(ESTADO_PENDIENTE);
            
            Transaccion transaccionGuardada = this.transaccionRepository.save(transaccion);
            registrarCambioEstado(transaccionGuardada, ESTADO_PENDIENTE, "Transacción creada");
            
            String nivelRiesgo = monitoreoFraudeService.evaluarRiesgoTransaccion(transaccionGuardada);
            if ("ALTO".equals(nivelRiesgo)) {
                return actualizarEstadoTransaccion(transaccionGuardada.getCodigo(), ESTADO_REVISION, 
                    "Transacción en revisión por riesgo alto");
            }
            
            return transaccionGuardada;
        } catch (Exception e) {
            throw new RuntimeException("Error al crear la transacción: " + e.getMessage());
        }
    }

    public Transaccion actualizarEstadoTransaccion(Integer id, String nuevoEstado, String detalle) {
        try {
            Transaccion transaccion = obtenerTransaccionPorId(id);
            validarCambioEstado(transaccion.getEstado(), nuevoEstado);
            
            transaccion.setEstado(nuevoEstado);
            transaccion = this.transaccionRepository.save(transaccion);
            registrarCambioEstado(transaccion, nuevoEstado, detalle);
            
            return transaccion;
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar el estado de la transacción: " + e.getMessage());
        }
    }

    private void validarTransaccion(Transaccion transaccion) {
        if (transaccion.getMonto() == null || transaccion.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("El monto debe ser mayor a cero");
        }
        if (transaccion.getBanco() == null) {
            throw new RuntimeException("El banco es requerido");
        }
        if (transaccion.getNumeroTarjeta() == null || transaccion.getNumeroTarjeta().trim().isEmpty()) {
            throw new RuntimeException("El número de tarjeta es requerido");
        }
    }

    private void validarCambioEstado(String estadoActual, String nuevoEstado) {
        if (ESTADO_RECHAZADA.equals(estadoActual)) {
            throw new RuntimeException("No se puede cambiar el estado de una transacción rechazada");
        }
        if (ESTADO_APROBADA.equals(estadoActual) && !ESTADO_REVISION.equals(nuevoEstado)) {
            throw new RuntimeException("Una transacción aprobada solo puede pasar a revisión");
        }
        if (!List.of(ESTADO_PENDIENTE, ESTADO_APROBADA, ESTADO_RECHAZADA, ESTADO_REVISION).contains(nuevoEstado)) {
            throw new RuntimeException("Estado no válido");
        }
    }

    private void registrarCambioEstado(Transaccion transaccion, String estado, String detalle) {
        try {
            HistorialEstadoTransaccion historial = new HistorialEstadoTransaccion();
            historial.setTransaccion(transaccion);
            historial.setEstado(estado);
            historial.setFechaEstadoCambio(LocalDateTime.now());
            historial.setDetalle(detalle);
            this.historialRepository.save(historial);
        } catch (Exception e) {
            throw new RuntimeException("Error al registrar el cambio de estado: " + e.getMessage());
        }
    }
}
