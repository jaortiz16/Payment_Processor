package com.banquito.cards.comision.service;

import com.banquito.cards.comision.model.Banco;
import com.banquito.cards.comision.model.Comision;
import com.banquito.cards.comision.repository.BancoRepository;
import com.banquito.cards.comision.repository.ComisionRepository;
import com.banquito.cards.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional
public class BancoService {

    private static final String ENTITY_NAME = "Banco";
    private static final String ESTADO_ACTIVO = "ACT";
    private static final String ESTADO_INACTIVO = "INA";
    private final BancoRepository bancoRepository;
    private final ComisionRepository comisionRepository;

    public BancoService(BancoRepository bancoRepository, ComisionRepository comisionRepository) {
        this.bancoRepository = bancoRepository;
        this.comisionRepository = comisionRepository;
    }

    @Transactional(readOnly = true)
    public List<Banco> obtenerBancosActivos() {
        log.info("Obteniendo lista de bancos activos");
        List<Banco> bancos = this.bancoRepository.findByEstado(ESTADO_ACTIVO);
        log.info("Se encontraron {} bancos activos", bancos.size());
        return bancos;
    }

    @Transactional(readOnly = true)
    public List<Banco> obtenerBancosPorRazonSocialYEstado(String razonSocial, String estado) {
        log.info("Buscando bancos por razón social: {} y estado: {}", razonSocial, estado);
        validarEstado(estado);
        List<Banco> bancos = this.bancoRepository.findByRazonSocialContainingAndEstado(razonSocial, estado);
        log.info("Se encontraron {} bancos con la razón social: {}", bancos.size(), razonSocial);
        return bancos;
    }

    @Transactional(readOnly = true)
    public List<Banco> obtenerBancosPorNombreYEstado(String nombreComercial, String estado) {
        log.info("Buscando bancos por nombre comercial: {} y estado: {}", nombreComercial, estado);
        validarEstado(estado);
        List<Banco> bancos = this.bancoRepository.findByNombreComercialContainingAndEstado(nombreComercial, estado);
        log.info("Se encontraron {} bancos con el nombre comercial: {}", bancos.size(), nombreComercial);
        return bancos;
    }

    @Transactional(readOnly = true)
    public Banco obtenerBancoPorId(Integer id) {
        log.info("Buscando banco por ID: {}", id);
        Banco banco = this.bancoRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("No se encontró banco con ID: {}", id);
                    return new NotFoundException(id.toString(), ENTITY_NAME);
                });
        log.info("Banco encontrado con ID: {}", id);
        return banco;
    }

    public Banco obtenerBancoPorRuc(String ruc) {
        log.info("Buscando banco por RUC: {}", ruc);
        Banco banco = bancoRepository.findByRucAndEstado(ruc, ESTADO_ACTIVO)
                .orElseThrow(() -> {
                    log.error("No se encontró banco activo con RUC: {}", ruc);
                    return new NotFoundException(ruc, ENTITY_NAME);
                });
        log.info("Banco encontrado con RUC: {}", ruc);
        return banco;
    }

    public Banco crearBanco(Banco banco) {
        log.info("Iniciando creación de nuevo banco con RUC: {}", banco.getRuc());
        validarBanco(banco);

        if (bancoRepository.existsByRuc(banco.getRuc())) {
            log.error("Error al crear banco: Ya existe un banco con el RUC: {}", banco.getRuc());
            throw new RuntimeException("Ya existe un banco con el RUC: " + banco.getRuc());
        }
        if (bancoRepository.existsByCodigoInterno(banco.getCodigoInterno())) {
            log.error("Error al crear banco: Ya existe un banco con el código interno: {}", banco.getCodigoInterno());
            throw new RuntimeException("Ya existe un banco con el código interno: " + banco.getCodigoInterno());
        }

        // Validar y cargar la comisión
        if (banco.getComision() != null && banco.getComision().getCodigo() != null) {
            log.info("Validando comisión con código: {}", banco.getComision().getCodigo());
            Comision comision = comisionRepository.findById(banco.getComision().getCodigo())
                    .orElseThrow(() -> {
                        log.error("No se encontró la comisión con código: {}", banco.getComision().getCodigo());
                        return new NotFoundException(banco.getComision().getCodigo().toString(), "Comision");
                    });
            banco.setComision(comision);
        }

        banco.setEstado(ESTADO_ACTIVO);
        banco.setFechaCreacion(LocalDateTime.now());
        Banco bancoCreado = this.bancoRepository.save(banco);
        log.info("Banco creado exitosamente con ID: {}", bancoCreado.getId());
        return bancoCreado;
    }

    public Banco actualizarBanco(Integer id, Banco banco) {
        log.info("Iniciando actualización de banco con ID: {}", id);
        Banco bancoExistente = obtenerBancoPorId(id);
        validarBanco(banco);

        if (!bancoExistente.getRuc().equals(banco.getRuc()) &&
                bancoRepository.existsByRuc(banco.getRuc())) {
            log.error("Error al actualizar banco: Ya existe un banco con el RUC: {}", banco.getRuc());
            throw new RuntimeException("Ya existe un banco con el RUC: " + banco.getRuc());
        }
        if (!bancoExistente.getCodigoInterno().equals(banco.getCodigoInterno()) &&
                bancoRepository.existsByCodigoInterno(banco.getCodigoInterno())) {
            log.error("Error al actualizar banco: Ya existe un banco con el código interno: {}",
                    banco.getCodigoInterno());
            throw new RuntimeException("Ya existe un banco con el código interno: " + banco.getCodigoInterno());
        }

        // Validar y cargar la comisión
        if (banco.getComision() != null && banco.getComision().getCodigo() != null) {
            log.info("Validando comisión con código: {}", banco.getComision().getCodigo());
            Comision comision = comisionRepository.findById(banco.getComision().getCodigo())
                    .orElseThrow(() -> {
                        log.error("No se encontró la comisión con código: {}", banco.getComision().getCodigo());
                        return new NotFoundException(banco.getComision().getCodigo().toString(), "Comision");
                    });
            bancoExistente.setComision(comision);
        }

        bancoExistente.setRazonSocial(banco.getRazonSocial());
        bancoExistente.setNombreComercial(banco.getNombreComercial());
        bancoExistente.setRuc(banco.getRuc());
        bancoExistente.setCodigoInterno(banco.getCodigoInterno());

        Banco bancoActualizado = this.bancoRepository.save(bancoExistente);
        log.info("Banco actualizado exitosamente con ID: {}", id);
        return bancoActualizado;
    }

    public void inactivarBanco(Integer id) {
        log.info("Iniciando inactivación de banco con ID: {}", id);
        Banco banco = obtenerBancoPorId(id);
        if (ESTADO_INACTIVO.equals(banco.getEstado())) {
            log.error("Error al inactivar banco: El banco con ID {} ya se encuentra inactivo", id);
            throw new RuntimeException("El banco ya se encuentra inactivo");
        }
        banco.setEstado(ESTADO_INACTIVO);
        banco.setFechaInactivacion(LocalDateTime.now());
        this.bancoRepository.save(banco);
        log.info("Banco inactivado exitosamente con ID: {}", id);
    }

    private void validarBanco(Banco banco) {
        if (banco.getRazonSocial() == null || banco.getRazonSocial().trim().isEmpty()) {
            log.error("Error de validación: La razón social es requerida");
            throw new RuntimeException("La razón social es requerida");
        }
        if (banco.getNombreComercial() == null || banco.getNombreComercial().trim().isEmpty()) {
            log.error("Error de validación: El nombre comercial es requerido");
            throw new RuntimeException("El nombre comercial es requerido");
        }
        if (banco.getRuc() == null || banco.getRuc().trim().isEmpty()) {
            log.error("Error de validación: El RUC es requerido");
            throw new RuntimeException("El RUC es requerido");
        }
        if (banco.getCodigoInterno() == null || banco.getCodigoInterno().trim().isEmpty()) {
            log.error("Error de validación: El código interno es requerido");
            throw new RuntimeException("El código interno es requerido");
        }
    }

    private void validarEstado(String estado) {
        if (!List.of(ESTADO_ACTIVO, ESTADO_INACTIVO).contains(estado)) {
            log.error("Error de validación: Estado inválido: {}. Use: ACT o INA", estado);
            throw new RuntimeException("Estado inválido. Use: ACT o INA");
        }
    }
}
