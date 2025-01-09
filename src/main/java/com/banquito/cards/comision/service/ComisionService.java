package com.banquito.cards.comision.service;

import com.banquito.cards.comision.model.Comision;
import com.banquito.cards.comision.model.ComisionSegmento;
import com.banquito.cards.comision.repository.ComisionRepository;
import com.banquito.cards.comision.repository.ComisionSegmentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class ComisionService {

    private final ComisionRepository comisionRepository;
    private final ComisionSegmentoRepository comisionSegmentoRepository;

    public ComisionService(ComisionRepository comisionRepository,
                         ComisionSegmentoRepository comisionSegmentoRepository) {
        this.comisionRepository = comisionRepository;
        this.comisionSegmentoRepository = comisionSegmentoRepository;
    }

    @Transactional(readOnly = true)
    public List<Comision> obtenerComisionesPorTipo(String tipo) {
        if (!List.of(Comision.TIPO_PORCENTAJE, Comision.TIPO_FIJO).contains(tipo)) {
            throw new RuntimeException("Tipo de comisión inválido. Use: POR o FIJ");
        }
        return this.comisionRepository.findByTipo(tipo);
    }

    @Transactional(readOnly = true)
    public List<Comision> obtenerComisionesPorMontoBaseEntre(BigDecimal montoMinimo, BigDecimal montoMaximo) {
        if (montoMinimo == null || montoMaximo == null) {
            throw new RuntimeException("Los montos mínimo y máximo son requeridos");
        }
        if (montoMinimo.compareTo(montoMaximo) > 0) {
            throw new RuntimeException("El monto mínimo no puede ser mayor al monto máximo");
        }
        return this.comisionRepository.findByMontoBaseBetween(montoMinimo, montoMaximo);
    }

    @Transactional(readOnly = true)
    public Comision obtenerComisionPorId(Integer id) {
        return this.comisionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No existe la comisión con id: " + id));
    }

    public Comision crearComision(Comision comision) {
        try {
            validarComision(comision);
            return this.comisionRepository.save(comision);
        } catch (Exception e) {
            throw new RuntimeException("Error al crear la comisión: " + e.getMessage());
        }
    }

    public Comision actualizarComision(Integer id, Comision comision) {
        try {
            Comision comisionExistente = obtenerComisionPorId(id);
            validarComision(comision);
            
            comisionExistente.setTipo(comision.getTipo());
            comisionExistente.setMontoBase(comision.getMontoBase());
            comisionExistente.setTransaccionesBase(comision.getTransaccionesBase());
            comisionExistente.setManejaSegmentos(comision.getManejaSegmentos());
            
            return this.comisionRepository.save(comisionExistente);
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar la comisión: " + e.getMessage());
        }
    }

    public ComisionSegmento agregarSegmento(Integer comisionId, ComisionSegmento segmento) {
        try {
            Comision comision = obtenerComisionPorId(comisionId);
            validarSegmento(comision, segmento);
            segmento.setComision(comision);
            return this.comisionSegmentoRepository.save(segmento);
        } catch (Exception e) {
            throw new RuntimeException("Error al agregar el segmento: " + e.getMessage());
        }
    }

    public BigDecimal calcularComision(Integer comisionId, Integer numeroTransacciones, BigDecimal montoTransaccion) {
        try {
            Comision comision = obtenerComisionPorId(comisionId);
            
            if (comision.getManejaSegmentos()) {
                return calcularComisionPorSegmento(comision, numeroTransacciones, montoTransaccion);
            } else {
                return calcularComisionBase(comision, montoTransaccion);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al calcular la comisión: " + e.getMessage());
        }
    }

    private void validarComision(Comision comision) {
        if (comision.getMontoBase() == null || comision.getMontoBase().compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("El monto base debe ser mayor o igual a cero");
        }
        if (comision.getTransaccionesBase() == null || comision.getTransaccionesBase() < 0) {
            throw new RuntimeException("El número de transacciones base debe ser mayor o igual a cero");
        }
        if (!List.of(Comision.TIPO_PORCENTAJE, Comision.TIPO_FIJO).contains(comision.getTipo())) {
            throw new RuntimeException("Tipo de comisión inválido. Use: POR o FIJ");
        }
    }

    private void validarSegmento(Comision comision, ComisionSegmento segmento) {
        if (!comision.getManejaSegmentos()) {
            throw new RuntimeException("Esta comisión no maneja segmentos");
        }
        if (segmento.getMonto() == null || segmento.getMonto().compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("El monto del segmento debe ser mayor o igual a cero");
        }
        if (segmento.getTransaccionesHasta() == null || 
            segmento.getTransaccionesHasta().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("El límite de transacciones debe ser mayor a cero");
        }
    }

    private BigDecimal calcularComisionBase(Comision comision, BigDecimal montoTransaccion) {
        if (Comision.TIPO_PORCENTAJE.equals(comision.getTipo())) {
            return montoTransaccion.multiply(comision.getMontoBase().divide(new BigDecimal(100)));
        } else {
            return comision.getMontoBase();
        }
    }

    private BigDecimal calcularComisionPorSegmento(Comision comision, Integer numeroTransacciones, 
                                                  BigDecimal montoTransaccion) {
        List<ComisionSegmento> segmentos = comisionSegmentoRepository.findByComision(comision);
        
        for (ComisionSegmento segmento : segmentos) {
            if (new BigDecimal(numeroTransacciones).compareTo(segmento.getTransaccionesHasta()) <= 0) {
                if (Comision.TIPO_PORCENTAJE.equals(comision.getTipo())) {
                    return montoTransaccion.multiply(segmento.getMonto().divide(new BigDecimal(100)));
                } else {
                    return segmento.getMonto();
                }
            }
        }
        
        return calcularComisionBase(comision, montoTransaccion);
    }
}
