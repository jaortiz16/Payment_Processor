package com.banquito.cards.seguridad.service;

import com.banquito.cards.seguridad.model.*;
import com.banquito.cards.seguridad.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
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
        log.info("Creando nueva configuración de seguridad para banco: {}", seguridadBanco);
        try {
            seguridadBanco.setFechaActualizacion(LocalDateTime.now());
            seguridadBanco.setEstado("ACT");
            SeguridadBanco savedBanco = seguridadBancoRepository.save(seguridadBanco);
            log.info("Configuración de seguridad para banco creada exitosamente: {}", savedBanco);
            return savedBanco;
        } catch (Exception e) {
            log.error("Error al crear configuración de seguridad para banco: {}", e.getMessage());
            throw e;
        }
    }

    public Optional<SeguridadBanco> obtenerSeguridadBancoPorId(Integer id){
        log.info("Buscando configuración de seguridad para banco con ID: {}", id);
        try {
            Optional<SeguridadBanco> banco = seguridadBancoRepository.findById(id);
            if (banco.isPresent()) {
                log.info("Configuración encontrada: {}", banco.get());
            } else {
                log.warn("No se encontró configuración para el banco con ID: {}", id);
            }
            return banco;
        } catch (Exception e) {
            log.error("Error al buscar configuración de seguridad para banco con ID: {}", id, e);
            throw e;
        }
    }

    @Transactional
    public SeguridadMarca actualizarSeguridadMarca(String marca, String nuevaClave){
        log.info("Intentando actualizar clave de seguridad para marca: {}, nuevaClave: {}", marca, nuevaClave);
        try {
            if (nuevaClave == null || nuevaClave.length() < 8) {
                log.error("La clave proporcionada no cumple con los requisitos de longitud mínima");
                throw new RuntimeException("La clave debe tener al menos 8 caracteres");
            }

            SeguridadMarca seguridadMarca = seguridadMarcaRepository.findById(marca)
                    .orElseGet(() -> new SeguridadMarca(marca));

            seguridadMarca.setClave(nuevaClave);
            seguridadMarca.setFechaActualizacion(LocalDateTime.now());
            SeguridadMarca updatedMarca = seguridadMarcaRepository.save(seguridadMarca);
            log.info("Clave de seguridad para marca actualizada exitosamente: {}", updatedMarca);
            return updatedMarca;
        } catch (Exception e) {
            log.error("Error al actualizar clave de seguridad para marca: {}, error: {}", marca, e.getMessage());
            throw e;
        }
    }

    @Transactional
    public SeguridadGateway crearSeguridadGateway(SeguridadGateway seguridadGateway) {
        log.info("Creando nueva configuración de seguridad para gateway: {}", seguridadGateway);
        try {
            seguridadGateway.setFechaCreacion(LocalDateTime.now());
            seguridadGateway.setEstado("ACT");
            SeguridadGateway savedGateway = seguridadGatewayRepository.save(seguridadGateway);
            log.info("Configuración de seguridad para gateway creada exitosamente: {}", savedGateway);
            return savedGateway;
        } catch (Exception e) {
            log.error("Error al crear configuración de seguridad para gateway: {}", e.getMessage());
            throw e;
        }
    }

    @Transactional
    public SeguridadProcesador actualizarSeguridadProcesador(Integer id, String nuevaClave){
        log.info("Intentando actualizar clave de seguridad para procesador con ID: {}, nuevaClave: {}", id, nuevaClave);
        try {
            if (nuevaClave == null || nuevaClave.length() < 8) {
                log.error("La clave proporcionada no cumple con los requisitos de longitud mínima");
                throw new RuntimeException("La clave debe tener al menos 8 caracteres");
            }

            SeguridadProcesador procesador = seguridadProcesadorRepository.findById(id)
                    .orElseThrow(() -> {
                        log.error("No se encontró seguridad para el procesador con ID: {}", id);
                        return new RuntimeException("Seguridad de procesador no encontrada");
                    });

            procesador.setClave(nuevaClave);
            procesador.setFechaActualizacion(LocalDateTime.now());
            SeguridadProcesador updatedProcesador = seguridadProcesadorRepository.save(procesador);
            log.info("Clave de seguridad para procesador actualizada exitosamente: {}", updatedProcesador);
            return updatedProcesador;
        } catch (Exception e) {
            log.error("Error al actualizar clave de seguridad para procesador con ID: {}, error: {}", id, e.getMessage());
            throw e;
        }
    }
}