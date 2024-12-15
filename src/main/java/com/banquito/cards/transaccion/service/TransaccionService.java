package com.banquito.cards.transaccion.service;

import com.banquito.cards.transaccion.model.Transaccion;
import com.banquito.cards.transaccion.model.HistorialEstadoTransaccion;
import com.banquito.cards.transaccion.repository.HistorialEstadoTransaccionRepository;
import com.banquito.cards.transaccion.repository.TransaccionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TransaccionService {

    private final TransaccionRepository transaccionRepository;
    private final HistorialEstadoTransaccionRepository historialRepository;

    public TransaccionService(TransaccionRepository transaccionRepository,
                            HistorialEstadoTransaccionRepository historialRepository) {
        this.transaccionRepository = transaccionRepository;
        this.historialRepository = historialRepository;
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
        transaccion.setEstado("PEN"); // Pendiente
        
        Transaccion transaccionGuardada = this.transaccionRepository.save(transaccion);
        
        // Registrar el estado inicial
        HistorialEstadoTransaccion historial = new HistorialEstadoTransaccion();
        historial.setTransaccion(transaccionGuardada);
        historial.setEstado("PEN");
        historial.setFechaEstadoCambio(LocalDateTime.now());
        historial.setDetalle("Transacción creada");
        
        this.historialRepository.save(historial);
        
        return transaccionGuardada;
    }

    @Transactional
    public Transaccion actualizarEstadoTransaccion(Integer id, String nuevoEstado, String detalle) {
        Transaccion transaccion = this.transaccionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Transacción no encontrada"));
        
        transaccion.setEstado(nuevoEstado);
        transaccion.setDetalle(detalle);
        
        // Registrar el cambio de estado
        HistorialEstadoTransaccion historial = new HistorialEstadoTransaccion();
        historial.setTransaccion(transaccion);
        historial.setEstado(nuevoEstado);
        historial.setFechaEstadoCambio(LocalDateTime.now());
        historial.setDetalle(detalle);
        
        this.historialRepository.save(historial);
        
        return this.transaccionRepository.save(transaccion);
    }

}
