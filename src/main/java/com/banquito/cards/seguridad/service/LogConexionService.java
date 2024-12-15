package com.banquito.cards.seguridad.service;

import com.banquito.cards.seguridad.model.LogConexion;
import com.banquito.cards.seguridad.model.SeguridadBanco;
import com.banquito.cards.seguridad.model.SeguridadMarca;
import com.banquito.cards.seguridad.repository.LogConexionRepository;
import com.banquito.cards.seguridad.repository.SeguridadBancoRepository;
import com.banquito.cards.seguridad.repository.SeguridadMarcaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LogConexionService {

    private final LogConexionRepository logConexionRepository;
    private final SeguridadBancoRepository seguridadBancoRepository;
    private final SeguridadMarcaRepository seguridadMarcaRepository;

    public LogConexionService(LogConexionRepository logConexionRepository,
                            SeguridadBancoRepository seguridadBancoRepository,
                            SeguridadMarcaRepository seguridadMarcaRepository) {
        this.logConexionRepository = logConexionRepository;
        this.seguridadBancoRepository = seguridadBancoRepository;
        this.seguridadMarcaRepository = seguridadMarcaRepository;
    }

    @Transactional
    public LogConexion registrarConexion(String marca, Integer codBanco, String ipOrigen,
                                       String operacion, String resultado) {
        // Validar que existan la marca y el banco
        SeguridadMarca seguridadMarca = seguridadMarcaRepository.findById(marca)
                .orElseThrow(() -> new RuntimeException("Marca no encontrada"));
        
        SeguridadBanco seguridadBanco = seguridadBancoRepository.findById(codBanco)
                .orElseThrow(() -> new RuntimeException("Banco no encontrado"));

        // Validar el estado de las credenciales
        if (!"ACT".equals(seguridadBanco.getEstado())) {
            throw new RuntimeException("Las credenciales del banco están inactivas");
        }

        LogConexion log = new LogConexion();
        log.setSeguridadMarca(seguridadMarca);
        log.setSeguridadBanco(seguridadBanco);
        log.setIpOrigen(ipOrigen);
        log.setOperacion(operacion);
        log.setResultado(resultado);
        log.setFecha(LocalDateTime.now());

        return logConexionRepository.save(log);
    }

    public List<LogConexion> obtenerLogsPorBanco(Integer codBanco) {
        return logConexionRepository.findBySeguridadBancoCode(codBanco);
    }

    public List<LogConexion> obtenerLogsPorMarca(String marca) {
        return logConexionRepository.findBySeguridadMarcaMarca(marca);
    }

    public List<LogConexion> obtenerLogsPorFecha(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return logConexionRepository.findByFechaBetweenOrderByFechaDesc(fechaInicio, fechaFin);
    }

    public List<LogConexion> obtenerLogsPorResultado(String resultado) {
        return logConexionRepository.findByResultado(resultado);
    }

    public List<LogConexion> obtenerLogsPorOperacion(String operacion) {
        return logConexionRepository.findByOperacion(operacion);
    }
}
