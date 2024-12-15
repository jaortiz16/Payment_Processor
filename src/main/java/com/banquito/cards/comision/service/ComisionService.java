package com.banquito.cards.comision.service;

import com.banquito.cards.comision.model.Comision;
import com.banquito.cards.comision.model.ComisionSegmento;
import com.banquito.cards.comision.repository.ComisionRepository;
import com.banquito.cards.comision.repository.ComisionSegmentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        return this.comisionRepository.save(comision);
    }

    @Transactional
    public Comision actualizar(Integer id, Comision comision) {
        Comision comisionExistente = this.comisionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comisión no encontrada"));

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

        if (!comision.getManejaSegmentos()) {
            throw new RuntimeException("Esta comisión no maneja segmentos");
        }

        segmento.setComision(comision);
        return this.comisionSegmentoRepository.save(segmento);
    }
}
