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

    @Transactional
    public SeguridadBanco crearSeguridadBanco(SeguridadBanco seguridadBanco) {
        validarCredencialesBanco(seguridadBanco);
        seguridadBanco.setFechaActualizacion(LocalDateTime.now());
        seguridadBanco.setEstado("ACT");
        return seguridadBancoRepository.save(seguridadBanco);
    }

    private void validarCredencialesBanco(SeguridadBanco seguridadBanco) {
        if (seguridadBanco.getClave() == null || seguridadBanco.getClave().length() < 8) {
            throw new RuntimeException("La clave debe tener al menos 8 caracteres");
        }
    }

    public Optional<SeguridadBanco> obtenerSeguridadBancoPorId(Integer id) {
        return seguridadBancoRepository.findById(id);
    }

    @Transactional
    public SeguridadMarca actualizarSeguridadMarca(String marca, String nuevaClave) {
        if (nuevaClave == null || nuevaClave.length() < 8) {
            throw new RuntimeException("La clave debe tener al menos 8 caracteres");
        }

        SeguridadMarca seguridadMarca = seguridadMarcaRepository.findById(marca)
                .orElseGet(() -> new SeguridadMarca(marca));
        
        seguridadMarca.setClave(nuevaClave);
        seguridadMarca.setFechaActualizacion(LocalDateTime.now());
        return seguridadMarcaRepository.save(seguridadMarca);
    }

    @Transactional
    public SeguridadGateway crearSeguridadGateway(SeguridadGateway seguridadGateway) {
        validarCredencialesGateway(seguridadGateway);
        seguridadGateway.setFechaCreacion(LocalDateTime.now());
        seguridadGateway.setEstado("ACT");
        return seguridadGatewayRepository.save(seguridadGateway);
    }

    private void validarCredencialesGateway(SeguridadGateway seguridadGateway) {
        if (seguridadGateway.getClave() == null || seguridadGateway.getClave().length() < 8) {
            throw new RuntimeException("La clave debe tener al menos 8 caracteres");
        }
    }

    @Transactional
    public SeguridadProcesador actualizarSeguridadProcesador(Integer id, String nuevaClave) {
        if (nuevaClave == null || nuevaClave.length() < 8) {
            throw new RuntimeException("La clave debe tener al menos 8 caracteres");
        }

        SeguridadProcesador procesador = seguridadProcesadorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Seguridad de procesador no encontrada"));
        
        procesador.setClave(nuevaClave);
        procesador.setFechaActualizacion(LocalDateTime.now());
        return seguridadProcesadorRepository.save(procesador);
    }

    @Transactional
    public LogConexion registrarConexion(String marca, Integer codBanco, String ipOrigen, 
                                       String operacion, String resultado) {
        SeguridadMarca seguridadMarca = seguridadMarcaRepository.findById(marca)
                .orElseThrow(() -> new RuntimeException("Marca no encontrada"));
        
        SeguridadBanco seguridadBanco = seguridadBancoRepository.findById(codBanco)
                .orElseThrow(() -> new RuntimeException("Banco no encontrado"));
        validarEstadoCredenciales(seguridadBanco);
        LogConexion log = new LogConexion();
        log.setSeguridadMarca(seguridadMarca);
        log.setSeguridadBanco(seguridadBanco);
        log.setIpOrigen(ipOrigen);
        log.setOperacion(operacion);
        log.setResultado(resultado);
        log.setFecha(LocalDateTime.now());
        
        return logConexionRepository.save(log);
    }

    private void validarEstadoCredenciales(SeguridadBanco seguridadBanco) {
        if (!"ACT".equals(seguridadBanco.getEstado())) {
            throw new RuntimeException("Las credenciales del banco están inactivas");
        }
        if (seguridadBanco.getFechaActualizacion().plusMonths(3).isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Las credenciales del banco han expirado");
        }
    }
} 