package com.banquito.cards.transaccion.service;

import com.banquito.cards.transaccion.model.HistorialEstadoTransaccion;
import com.banquito.cards.transaccion.model.Transaccion;
import com.banquito.cards.transaccion.repository.HistorialEstadoTransaccionRepository;
import com.banquito.cards.transaccion.repository.TransaccionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class HistorialEstadoTransaccionService {

    private final HistorialEstadoTransaccionRepository historialRepository;
    private final TransaccionRepository transaccionRepository;

    public HistorialEstadoTransaccionService(
            HistorialEstadoTransaccionRepository historialRepository,
            TransaccionRepository transaccionRepository) {
        this.historialRepository = historialRepository;
        this.transaccionRepository = transaccionRepository;
    }

    @Transactional
    public HistorialEstadoTransaccion registrarCambioEstado(
            Integer transaccionId, String nuevoEstado, String detalle) {
        
        Transaccion transaccion = transaccionRepository.findById(transaccionId)
                .orElseThrow(() -> new RuntimeException("Transacción no encontrada"));

        validarTransicionEstado(transaccion.getEstado(), nuevoEstado);

        HistorialEstadoTransaccion historial = new HistorialEstadoTransaccion();
        historial.setTransaccion(transaccion);
        historial.setEstado(nuevoEstado);
        historial.setFechaEstadoCambio(LocalDateTime.now());
        historial.setDetalle(detalle);
        transaccion.setEstado(nuevoEstado);
        transaccion.setDetalle(detalle);
        transaccionRepository.save(transaccion);

        return historialRepository.save(historial);
    }

    private void validarTransicionEstado(String estadoActual, String nuevoEstado) {

        if (estadoActual.equals("REC")) {
            throw new RuntimeException("No se pueden realizar cambios en una transacción rechazada");
        }
        
        if (estadoActual.equals("APR") && !nuevoEstado.equals("REV")) {
            throw new RuntimeException("Una transacción aprobada solo puede ser reversada");
        }
        
        if (estadoActual.equals("PEN") && 
            !nuevoEstado.equals("APR") && 
            !nuevoEstado.equals("REC")) {
            throw new RuntimeException(
                "Una transacción pendiente solo puede ser aprobada o rechazada");
        }
    }
}
