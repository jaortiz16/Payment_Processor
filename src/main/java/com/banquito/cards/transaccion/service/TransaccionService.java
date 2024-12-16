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

@Service
public class TransaccionService {

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

    public List<Transaccion> obtenerTodasLasTransacciones() {
        return this.transaccionRepository.findAll();
    }

    public Optional<Transaccion> obtenerTransaccionPorId(Integer id) {
        return this.transaccionRepository.findById(id);
    }

    @Transactional
    public Transaccion crearTransaccion(Transaccion transaccion) {
        transaccion.setFechaCreacion(LocalDateTime.now());
        transaccion.setEstado("PEN");
        Transaccion transaccionGuardada = this.transaccionRepository.save(transaccion);
        registrarCambioEstado(transaccionGuardada, "PEN", "Transacción creada");
        String nivelRiesgo = monitoreoFraudeService.evaluarRiesgoTransaccion(transaccionGuardada);
        if ("ALTO".equals(nivelRiesgo)) {
            actualizarEstadoTransaccion(transaccionGuardada.getCode(), "REV", "Transacción en revisión por riesgo alto");
        }

        return transaccionGuardada;
    }

    @Transactional
    public Transaccion actualizarEstadoTransaccion(Integer id, String nuevoEstado, String detalle) {
        Transaccion transaccion = this.transaccionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Transacción no encontrada"));

        validarCambioEstado(transaccion.getEstado(), nuevoEstado);

        transaccion.setEstado(nuevoEstado);
        transaccion = this.transaccionRepository.save(transaccion);

        registrarCambioEstado(transaccion, nuevoEstado, detalle);

        return transaccion;
    }

    private void validarCambioEstado(String estadoActual, String nuevoEstado) {
        if ("APR".equals(estadoActual) || "REC".equals(estadoActual)) {
            throw new RuntimeException("No se puede cambiar el estado de una transacción finalizada");
        }
        if (!List.of("PEN", "APR", "REC", "REV").contains(nuevoEstado)) {
            throw new RuntimeException("Estado no válido");
        }
    }

    private void registrarCambioEstado(Transaccion transaccion, String estado, String detalle) {
        HistorialEstadoTransaccion historial = new HistorialEstadoTransaccion();
        historial.setTransaccion(transaccion);
        historial.setEstado(estado);
        historial.setFechaEstadoCambio(LocalDateTime.now());
        historial.setDetalle(detalle);

        this.historialRepository.save(historial);
    }
}
