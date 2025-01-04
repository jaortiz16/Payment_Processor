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
    }

    @Transactional
    public SeguridadBanco crearSeguridadBanco(SeguridadBanco seguridadBanco) {
        seguridadBanco.setFechaActualizacion(LocalDateTime.now());
        seguridadBanco.setEstado("ACT");
        return seguridadBancoRepository.save(seguridadBanco);
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
        seguridadGateway.setFechaCreacion(LocalDateTime.now());
        seguridadGateway.setEstado("ACT");
        return seguridadGatewayRepository.save(seguridadGateway);
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
} 