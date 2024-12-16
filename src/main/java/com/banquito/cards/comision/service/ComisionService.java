package com.banquito.cards.comision.service;

import com.banquito.cards.comision.model.Comision;
import com.banquito.cards.comision.model.ComisionPK;
import com.banquito.cards.comision.model.ComisionSegmento;
import com.banquito.cards.comision.repository.ComisionRepository;
import com.banquito.cards.comision.repository.ComisionSegmentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ComisionService {

    private final ComisionRepository comisionRepository;
    private final ComisionSegmentoRepository comisionSegmentoRepository;

    public ComisionService(ComisionRepository comisionRepository,
                           ComisionSegmentoRepository comisionSegmentoRepository) {
        this.comisionRepository = comisionRepository;
        this.comisionSegmentoRepository = comisionSegmentoRepository;
    }

    public List<Comision> obtenerTodas() {
        return this.comisionRepository.findAll();
    }

    public Optional<Comision> obtenerPorId(Integer id) {
        return this.comisionRepository.findById(id);
    }

    @Transactional
    public Comision crear(Comision comision) {
        validarComision(comision);
        return this.comisionRepository.save(comision);
    }

    private void validarComision(Comision comision) {
        if (comision.getMontoBase() == null || comision.getMontoBase().compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("El monto base debe ser mayor o igual a cero");
        }

        if (comision.getTransaccionesBase() == null || comision.getTransaccionesBase() < 0) {
            throw new RuntimeException("El número de transacciones base debe ser mayor o igual a cero");
        }

        if (!List.of("POR", "FIJ").contains(comision.getTipo())) {
            throw new RuntimeException("El tipo de comisión debe ser POR (Porcentaje) o FIJ (Fijo)");
        }
    }

    @Transactional
    public Comision actualizar(Integer id, Comision comision) {
        Comision comisionExistente = this.comisionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comisión no encontrada"));

        validarComision(comision);

        comisionExistente.setTipo(comision.getTipo());
        comisionExistente.setMontoBase(comision.getMontoBase());
        comisionExistente.setTransaccionesBase(comision.getTransaccionesBase());
        comisionExistente.setManejaSegmentos(comision.getManejaSegmentos());

        return this.comisionRepository.save(comisionExistente);
    }

    @Transactional
    public ComisionSegmento agregarSegmento(Integer comisionId, ComisionSegmento segmento) {
        Comision comision = this.comisionRepository.findById(comisionId)
                .orElseThrow(() -> new RuntimeException("Comisión no encontrada"));

        validarSegmento(comision, segmento);

        ComisionPK pk = new ComisionPK();
        pk.setCodComision(Integer.valueOf(comisionId));
        pk.setCodSegmento(segmento.getTransaccionesHasta());
        segmento.setPk(pk);
        
        segmento.setComision(comision);
        return this.comisionSegmentoRepository.save(segmento);
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

    public BigDecimal calcularComision(Integer comisionId, Integer numTransacciones, BigDecimal montoTransaccion) {
        Comision comision = this.comisionRepository.findById(comisionId)
                .orElseThrow(() -> new RuntimeException("Comisión no encontrada"));

        if (comision.getManejaSegmentos()) {
            return calcularComisionPorSegmento(comision, numTransacciones, montoTransaccion);
        } else {
            return calcularComisionBase(comision, montoTransaccion);
        }
    }

    private BigDecimal calcularComisionBase(Comision comision, BigDecimal montoTransaccion) {
        if ("POR".equals(comision.getTipo())) {
            return montoTransaccion.multiply(comision.getMontoBase().divide(new BigDecimal(100)));
        } else {
            return comision.getMontoBase();
        }
    }

    private BigDecimal calcularComisionPorSegmento(Comision comision, Integer numTransacciones, 
                                                  BigDecimal montoTransaccion) {
        List<ComisionSegmento> segmentos = comisionSegmentoRepository.findByComision(comision);
        
        for (ComisionSegmento segmento : segmentos) {
            if (new BigDecimal(numTransacciones).compareTo(segmento.getTransaccionesHasta()) <= 0) {
                if ("POR".equals(comision.getTipo())) {
                    return montoTransaccion.multiply(segmento.getMonto().divide(new BigDecimal(100)));
                } else {
                    return segmento.getMonto();
                }
            }
        }
        
        return calcularComisionBase(comision, montoTransaccion);
    }
}
