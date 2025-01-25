package com.banquito.cards.seguridad.service;

import com.banquito.cards.seguridad.model.LogConexion;
import com.banquito.cards.seguridad.model.SeguridadBanco;
import com.banquito.cards.seguridad.model.SeguridadMarca;
import com.banquito.cards.seguridad.repository.LogConexionRepository;
import com.banquito.cards.seguridad.repository.SeguridadBancoRepository;
import com.banquito.cards.seguridad.repository.SeguridadMarcaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
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
        log.info("Intentando registrar una conexión: marca={}, codBanco={}, ipOrigen={}, operacion={}, resultado={}",
                marca, codBanco, ipOrigen, operacion, resultado);

        SeguridadMarca seguridadMarca;
        SeguridadBanco seguridadBanco;

        try {
            seguridadMarca = seguridadMarcaRepository.findById(marca)
                    .orElseThrow(() -> {
                        log.error("Marca no encontrada: {}", marca);
                        return new RuntimeException("Marca no encontrada");
                    });

            seguridadBanco = seguridadBancoRepository.findById(codBanco)
                    .orElseThrow(() -> {
                        log.error("Banco no encontrado: codBanco={}", codBanco);
                        return new RuntimeException("Banco no encontrado");
                    });

            if (!"ACT".equals(seguridadBanco.getEstado())) {
                log.error("Las credenciales del banco están inactivas: codBanco={}", codBanco);
                throw new RuntimeException("Las credenciales del banco están inactivas");
            }

        } catch (RuntimeException e) {
            log.error("Error en la validación de la conexión: {}", e.getMessage());
            throw e;
        }

        try {
            LogConexion logConexion = new LogConexion();
            logConexion.setSeguridadMarca(seguridadMarca);
            logConexion.setSeguridadBanco(seguridadBanco);
            logConexion.setIpOrigen(ipOrigen);
            logConexion.setOperacion(operacion);
            logConexion.setResultado(resultado);
            logConexion.setFecha(LocalDateTime.now());

            LogConexion savedLog = logConexionRepository.save(logConexion);
            log.info("Registro de conexión exitoso: {}", savedLog);
            return savedLog;

        } catch (Exception e) {
            log.error("Error al registrar la conexión: {}", e.getMessage());
            throw new RuntimeException("Error al registrar la conexión", e);
        }
    }
}
