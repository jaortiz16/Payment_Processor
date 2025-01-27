package com.banquito.cards.comision.service;

import com.banquito.cards.comision.model.Comision;
import com.banquito.cards.comision.model.ComisionSegmento;
import com.banquito.cards.comision.repository.ComisionRepository;
import com.banquito.cards.comision.repository.ComisionSegmentoRepository;
import com.banquito.cards.exception.NotFoundException;
import com.banquito.cards.exception.BusinessException;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ComisionService {

    private static final String ENTITY_NAME = "Comision";
    private final ComisionRepository comisionRepository;
    private final ComisionSegmentoRepository comisionSegmentoRepository;

    public ComisionService(ComisionRepository comisionRepository,
                         ComisionSegmentoRepository comisionSegmentoRepository) {
        this.comisionRepository = comisionRepository;
        this.comisionSegmentoRepository = comisionSegmentoRepository;
    }

    @Transactional(readOnly = true)
    public List<Comision> obtenerTodasLasComisiones(String sortBy, String sortDir) {
        Sort.Direction direction = Sort.Direction.fromString(sortDir.toUpperCase());
        Sort sort = Sort.by(direction, sortBy);
        return this.comisionRepository.findAll(sort);
    }

    @Transactional(readOnly = true)
    public List<Comision> obtenerComisionesPorTipo(String tipo) {
        if (!List.of(Comision.TIPO_PORCENTAJE, Comision.TIPO_FIJO).contains(tipo)) {
            throw new BusinessException(tipo, ENTITY_NAME, "validar - tipo inválido");
        }
        return this.comisionRepository.findByTipo(tipo);
    }

    @Transactional(readOnly = true)
    public List<Comision> obtenerComisionesPorMontoBaseEntre(BigDecimal montoMinimo, BigDecimal montoMaximo) {
        if (montoMinimo == null || montoMaximo == null) {
            throw new BusinessException("montos mínimo y máximo", ENTITY_NAME, "validar - campos requeridos");
        }
        if (montoMinimo.compareTo(montoMaximo) > 0) {
            throw new BusinessException(montoMinimo + " > " + montoMaximo, ENTITY_NAME, "validar - monto mínimo mayor al máximo");
        }
        return this.comisionRepository.findByMontoBaseBetween(montoMinimo, montoMaximo);
    }

    @Transactional(readOnly = true)
    public Comision obtenerComisionPorId(Integer id) {
        return this.comisionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(id.toString(), ENTITY_NAME));
    }

    public Comision crearComision(Comision comision) {
        validarComision(comision);
        if (comisionRepository.existsByTipoAndMontoBase(comision.getTipo(), comision.getMontoBase())) {
            throw new BusinessException(comision.getTipo() + "-" + comision.getMontoBase(), ENTITY_NAME, "crear - comisión duplicada");
        }
        comision.setFechaCreacion(LocalDateTime.now());
        return this.comisionRepository.save(comision);
    }

    public Comision actualizarComision(Integer id, Comision comision) {
        Comision comisionExistente = obtenerComisionPorId(id);
        validarComision(comision);
        
        if (!comisionExistente.getTipo().equals(comision.getTipo()) && 
            comisionRepository.existsByTipoAndMontoBase(comision.getTipo(), comision.getMontoBase())) {
            throw new BusinessException(comision.getTipo() + "-" + comision.getMontoBase(), ENTITY_NAME, "actualizar - comisión duplicada");
        }
        
        comisionExistente.setTipo(comision.getTipo());
        comisionExistente.setMontoBase(comision.getMontoBase());
        comisionExistente.setTransaccionesBase(comision.getTransaccionesBase());
        comisionExistente.setManejaSegmentos(comision.getManejaSegmentos());
        
        return this.comisionRepository.save(comisionExistente);
    }

    public ComisionSegmento agregarSegmento(Integer comisionId, ComisionSegmento segmento) {
        Comision comision = obtenerComisionPorId(comisionId);
        validarSegmento(comision, segmento);
        
        if (comisionSegmentoRepository.existsByComisionAndTransaccionesHasta(comision, segmento.getTransaccionesHasta())) {
            throw new BusinessException(segmento.getTransaccionesHasta().toString(), "ComisionSegmento", "crear - límite de transacciones duplicado");
        }
        
        segmento.setComision(comision);
        return this.comisionSegmentoRepository.save(segmento);
    }

    public BigDecimal calcularComision(Integer comisionId, Integer numeroTransacciones, BigDecimal montoTransaccion) {
        Comision comision = obtenerComisionPorId(comisionId);
        
        if (comision.getManejaSegmentos()) {
            return calcularComisionPorSegmento(comision, numeroTransacciones, montoTransaccion);
        } else {
            return calcularComisionBase(comision, montoTransaccion);
        }
    }

    private void validarComision(Comision comision) {
        if (comision.getMontoBase() == null || comision.getMontoBase().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("monto base", ENTITY_NAME, "validar - debe ser mayor o igual a cero");
        }
        if (comision.getTransaccionesBase() == null || comision.getTransaccionesBase() < 0) {
            throw new BusinessException("transacciones base", ENTITY_NAME, "validar - debe ser mayor o igual a cero");
        }
        if (!List.of(Comision.TIPO_PORCENTAJE, Comision.TIPO_FIJO).contains(comision.getTipo())) {
            throw new BusinessException(comision.getTipo(), ENTITY_NAME, "validar - tipo inválido");
        }
    }

    private void validarSegmento(Comision comision, ComisionSegmento segmento) {
        if (!comision.getManejaSegmentos()) {
            throw new BusinessException(comision.getCodigo().toString(), ENTITY_NAME, "validar - no maneja segmentos");
        }
        if (segmento.getMonto() == null || segmento.getMonto().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("monto", "ComisionSegmento", "validar - debe ser mayor o igual a cero");
        }
        if (segmento.getTransaccionesHasta() == null || 
            segmento.getTransaccionesHasta().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("transacciones hasta", "ComisionSegmento", "validar - debe ser mayor a cero");
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
        List<ComisionSegmento> segmentos = comisionSegmentoRepository.findByComisionOrderByPkCodSegmentoAsc(comision);
        
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
