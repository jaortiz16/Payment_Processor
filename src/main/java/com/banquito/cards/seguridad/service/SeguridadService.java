package com.banquito.cards.seguridad.service;

import com.banquito.cards.seguridad.model.*;
import com.banquito.cards.seguridad.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class SeguridadService {

    private final SeguridadBancoRepository seguridadBancoRepository;
    private final SeguridadMarcaRepository seguridadMarcaRepository;
    private final SeguridadGatewayRepository seguridadGatewayRepository;
    private final SeguridadProcesadorRepository seguridadProcesadorRepository;
    private final LogConexionRepository logConexionRepository;

    public SeguridadService(
            SeguridadBancoRepository seguridadBancoRepository,
            SeguridadMarcaRepository seguridadMarcaRepository,
            SeguridadGatewayRepository seguridadGatewayRepository,
            SeguridadProcesadorRepository seguridadProcesadorRepository,
            LogConexionRepository logConexionRepository) {
        this.seguridadBancoRepository = seguridadBancoRepository;
        this.seguridadMarcaRepository = seguridadMarcaRepository;
        this.seguridadGatewayRepository = seguridadGatewayRepository;
        this.seguridadProcesadorRepository = seguridadProcesadorRepository;
        this.logConexionRepository = logConexionRepository;
    }

    // Métodos para SeguridadBanco
    @Transactional
    public SeguridadBanco crearSeguridadBanco(SeguridadBanco seguridadBanco) {
        seguridadBanco.setFechaActualizacion(LocalDateTime.now());
        seguridadBanco.setEstado("ACT");
        return seguridadBancoRepository.save(seguridadBanco);
    }

    public Optional<SeguridadBanco> obtenerSeguridadBancoPorId(Integer id) {
        return seguridadBancoRepository.findById(id);
    }

    // Métodos para SeguridadMarca
    @Transactional
    public SeguridadMarca actualizarSeguridadMarca(String marca, String nuevaClave) {
        SeguridadMarca seguridadMarca = seguridadMarcaRepository.findById(marca)
                .orElseGet(() -> new SeguridadMarca(marca));
        
        seguridadMarca.setClave(nuevaClave);
        seguridadMarca.setFechaActualizacion(LocalDateTime.now());
        return seguridadMarcaRepository.save(seguridadMarca);
    }

    // Métodos para SeguridadGateway
    @Transactional
    public SeguridadGateway crearSeguridadGateway(SeguridadGateway seguridadGateway) {
        seguridadGateway.setFechaCreacion(LocalDateTime.now());
        seguridadGateway.setEstado("ACT");
        return seguridadGatewayRepository.save(seguridadGateway);
    }

    // Métodos para SeguridadProcesador
    @Transactional
    public SeguridadProcesador actualizarSeguridadProcesador(Integer id, String nuevaClave) {
        SeguridadProcesador procesador = seguridadProcesadorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Seguridad de procesador no encontrada"));
        
        procesador.setClave(nuevaClave);
        procesador.setFechaActualizacion(LocalDateTime.now());
        return seguridadProcesadorRepository.save(procesador);
    }

    // Métodos para LogConexion
    @Transactional
    public LogConexion registrarConexion(String marca, Integer codBanco, String ipOrigen, 
                                       String operacion, String resultado) {
        LogConexion log = new LogConexion();
        log.setSeguridadMarca(seguridadMarcaRepository.findById(marca)
                .orElseThrow(() -> new RuntimeException("Marca no encontrada")));
        log.setSeguridadBanco(seguridadBancoRepository.findById(codBanco)
                .orElseThrow(() -> new RuntimeException("Banco no encontrado")));
        log.setIpOrigen(ipOrigen);
        log.setOperacion(operacion);
        log.setResultado(resultado);
        log.setFecha(LocalDateTime.now());
        
        return logConexionRepository.save(log);
    }
} 