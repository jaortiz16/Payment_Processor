package com.banquito.cards.comision.service;

import com.banquito.cards.comision.model.Comision;
import com.banquito.cards.comision.model.ComisionSegmento;
import com.banquito.cards.comision.repository.ComisionRepository;
import com.banquito.cards.comision.repository.ComisionSegmentoRepository;
import com.banquito.cards.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
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
    public List<Comision> obtenerComisionesPorTipo(String tipo) {
        log.info("Buscando comisiones por tipo: {}", tipo);
        if (!List.of(Comision.TIPO_PORCENTAJE, Comision.TIPO_FIJO).contains(tipo)) {
            log.error("Error de validación: Tipo de comisión inválido: {}", tipo);
            throw new RuntimeException("Tipo de comisión inválido. Use: POR o FIJ");
        }
        List<Comision> comisiones = this.comisionRepository.findByTipo(tipo);
        log.info("Se encontraron {} comisiones del tipo: {}", comisiones.size(), tipo);
        return comisiones;
    }

    @Transactional(readOnly = true)
    public List<Comision> obtenerComisionesPorMontoBaseEntre(BigDecimal montoMinimo, BigDecimal montoMaximo) {
        log.info("Buscando comisiones por monto base entre {} y {}", montoMinimo, montoMaximo);
        if (montoMinimo == null || montoMaximo == null) {
            log.error("Error de validación: Los montos mínimo y máximo son requeridos");
            throw new RuntimeException("Los montos mínimo y máximo son requeridos");
        }
        if (montoMinimo.compareTo(montoMaximo) > 0) {
            log.error("Error de validación: El monto mínimo ({}) no puede ser mayor al monto máximo ({})", 
                    montoMinimo, montoMaximo);
            throw new RuntimeException("El monto mínimo no puede ser mayor al monto máximo");
        }
        List<Comision> comisiones = this.comisionRepository.findByMontoBaseBetween(montoMinimo, montoMaximo);
        log.info("Se encontraron {} comisiones en el rango de montos especificado", comisiones.size());
        return comisiones;
    }

    @Transactional(readOnly = true)
    public Comision obtenerComisionPorId(Integer id) {
        log.info("Buscando comisión por ID: {}", id);
        Comision comision = this.comisionRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("No se encontró comisión con ID: {}", id);
                    return new NotFoundException(id.toString(), ENTITY_NAME);
                });
        log.info("Comisión encontrada con ID: {}", id);
        return comision;
    }

    public Comision crearComision(Comision comision) {
        log.info("Iniciando creación de nueva comisión de tipo: {}", comision.getTipo());
        validarComision(comision);
        
        if (comisionRepository.existsByTipoAndMontoBase(comision.getTipo(), comision.getMontoBase())) {
            log.error("Error al crear comisión: Ya existe una comisión con el tipo {} y monto base {}", 
                    comision.getTipo(), comision.getMontoBase());
            throw new RuntimeException("Ya existe una comisión con el mismo tipo y monto base");
        }
        
        comision.setFechaCreacion(LocalDateTime.now());
        Comision comisionCreada = this.comisionRepository.save(comision);
        log.info("Comisión creada exitosamente con ID: {}", comisionCreada.getId());
        return comisionCreada;
    }

    public Comision actualizarComision(Integer id, Comision comision) {
        log.info("Iniciando actualización de comisión con ID: {}", id);
        Comision comisionExistente = obtenerComisionPorId(id);
        validarComision(comision);
        
        if (!comisionExistente.getTipo().equals(comision.getTipo()) && 
            comisionRepository.existsByTipoAndMontoBase(comision.getTipo(), comision.getMontoBase())) {
            log.error("Error al actualizar comisión: Ya existe una comisión con el tipo {} y monto base {}", 
                    comision.getTipo(), comision.getMontoBase());
            throw new RuntimeException("Ya existe una comisión con el mismo tipo y monto base");
        }
        
        comisionExistente.setTipo(comision.getTipo());
        comisionExistente.setMontoBase(comision.getMontoBase());
        comisionExistente.setTransaccionesBase(comision.getTransaccionesBase());
        comisionExistente.setManejaSegmentos(comision.getManejaSegmentos());
        
        Comision comisionActualizada = this.comisionRepository.save(comisionExistente);
        log.info("Comisión actualizada exitosamente con ID: {}", id);
        return comisionActualizada;
    }

    public ComisionSegmento agregarSegmento(Integer comisionId, ComisionSegmento segmento) {
        log.info("Iniciando agregación de segmento a comisión con ID: {}", comisionId);
        Comision comision = obtenerComisionPorId(comisionId);
        validarSegmento(comision, segmento);
        
        if (comisionSegmentoRepository.existsByComisionAndTransaccionesHasta(comision, segmento.getTransaccionesHasta())) {
            log.error("Error al agregar segmento: Ya existe un segmento con el límite de transacciones: {}", 
                    segmento.getTransaccionesHasta());
            throw new RuntimeException("Ya existe un segmento con el mismo límite de transacciones");
        }
        
        segmento.setComision(comision);
        ComisionSegmento segmentoCreado = this.comisionSegmentoRepository.save(segmento);
        log.info("Segmento agregado exitosamente a comisión con ID: {}", comisionId);
        return segmentoCreado;
    }

    public BigDecimal calcularComision(Integer comisionId, Integer numeroTransacciones, BigDecimal montoTransaccion) {
        log.info("Calculando comisión para ID: {}, número de transacciones: {}, monto: {}", 
                comisionId, numeroTransacciones, montoTransaccion);
        Comision comision = obtenerComisionPorId(comisionId);
        
        BigDecimal comisionCalculada;
        if (comision.getManejaSegmentos()) {
            comisionCalculada = calcularComisionPorSegmento(comision, numeroTransacciones, montoTransaccion);
        } else {
            comisionCalculada = calcularComisionBase(comision, montoTransaccion);
        }
        
        log.info("Comisión calculada: {} para ID: {}", comisionCalculada, comisionId);
        return comisionCalculada;
    }

    private void validarComision(Comision comision) {
        if (comision.getMontoBase() == null || comision.getMontoBase().compareTo(BigDecimal.ZERO) < 0) {
            log.error("Error de validación: El monto base debe ser mayor o igual a cero");
            throw new RuntimeException("El monto base debe ser mayor o igual a cero");
        }
        if (comision.getTransaccionesBase() == null || comision.getTransaccionesBase() < 0) {
            log.error("Error de validación: El número de transacciones base debe ser mayor o igual a cero");
            throw new RuntimeException("El número de transacciones base debe ser mayor o igual a cero");
        }
        if (!List.of(Comision.TIPO_PORCENTAJE, Comision.TIPO_FIJO).contains(comision.getTipo())) {
            log.error("Error de validación: Tipo de comisión inválido: {}", comision.getTipo());
            throw new RuntimeException("Tipo de comisión inválido. Use: POR o FIJ");
        }
    }

    private void validarSegmento(Comision comision, ComisionSegmento segmento) {
        if (!comision.getManejaSegmentos()) {
            log.error("Error de validación: La comisión con código {} no maneja segmentos", comision.getCodigo());
            throw new RuntimeException("Esta comisión no maneja segmentos");
        }
        if (segmento.getMonto() == null || segmento.getMonto().compareTo(BigDecimal.ZERO) < 0) {
            log.error("Error de validación: El monto del segmento debe ser mayor o igual a cero");
            throw new RuntimeException("El monto del segmento debe ser mayor o igual a cero");
        }
        if (segmento.getTransaccionesHasta() == null || 
            segmento.getTransaccionesHasta().compareTo(BigDecimal.ZERO) <= 0) {
            log.error("Error de validación: El límite de transacciones debe ser mayor a cero");
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
